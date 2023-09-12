package com.example.twogether.user.service;

import com.example.twogether.board.repository.BoardColRepository;
import com.example.twogether.board.repository.BoardRepository;
import com.example.twogether.card.repository.CardLabelRepository;
import com.example.twogether.card.repository.CardRepository;
import com.example.twogether.checklist.repository.CheckListRepository;
import com.example.twogether.checklist.repository.ChlItemRepository;
import com.example.twogether.comment.repository.CommentRepository;
import com.example.twogether.common.error.CustomErrorCode;
import com.example.twogether.common.exception.CustomException;
import com.example.twogether.common.jwt.JwtUtil;
import com.example.twogether.common.redis.RedisEmail;
import com.example.twogether.common.redis.RedisRefreshToken;
import com.example.twogether.common.s3.S3Uploader;
import com.example.twogether.deck.repository.DeckRepository;
import com.example.twogether.user.dto.EditPasswordRequestDto;
import com.example.twogether.user.dto.EditUserRequestDto;
import com.example.twogether.user.dto.SignupRequestDto;
import com.example.twogether.user.entity.User;
import com.example.twogether.user.entity.UserPassword;
import com.example.twogether.user.entity.UserRoleEnum;
import com.example.twogether.user.repository.UserPasswordRepository;
import com.example.twogether.user.repository.UserRepository;
import com.example.twogether.workspace.repository.WpColRepository;
import com.example.twogether.workspace.repository.WpRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserPasswordRepository userPasswordRepository;
    private final WpRepository wpRepository;
    private final WpColRepository wpColRepository;
    private final BoardRepository boardRepository;
    private final BoardColRepository boardColRepository;
    private final DeckRepository deckRepository;
    private final CardRepository cardRepository;
    private final CommentRepository commentRepository;
    private final ChlItemRepository chlItemRepository;
    private final CheckListRepository checkListRepository;
    private final CardLabelRepository cardLabelRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisEmail redisUtil;
    private final RedisRefreshToken redisRefreshToken;
    private final S3Uploader s3Uploader;

    @Value("${admin.token}")
    private String adminToken;

    @Transactional
    public User signup(SignupRequestDto requestDto) {
        String password = passwordEncoder.encode(requestDto.getPassword());

        if (!redisUtil.hasKey(requestDto.getEmail()) || !redisUtil.isVerified(
            requestDto.getEmail())) {
            throw new CustomException(CustomErrorCode.EMAIL_NOT_VERIFIED);
        }

        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDto.isAdmin() && requestDto.getAdminToken().equals(adminToken)) {
            role = UserRoleEnum.ADMIN;
        }

        User user = userRepository.save(requestDto.toEntity(password, role));
        userPasswordRepository.save(UserPassword.builder().password(password).user(user).build());
        return user;
    }

    @Transactional
    public User editUserInfo(EditUserRequestDto requestDto, User user) {
        User found = findUser(user.getId());

        found.editUserInfo(requestDto.getNickname(), requestDto.getIntroduction());
        return found;
    }

    @Transactional
    public User editUserPassword(EditPasswordRequestDto requestDto, User user) {
        User found = findUser(user.getId());

        checkPassword(requestDto.getPassword(), found.getPassword());       // 기존 비밀번호 일치 여부 확인
        checkRecentPasswords(found.getId(),
            requestDto.getNewPassword());   // 바로 직전 혹은 기존에 사용 중인 비밀번호인지 확인

        // 새 비밀번호 저장
        String newPassword = passwordEncoder.encode(requestDto.getNewPassword());
        userPasswordRepository.save(
            UserPassword.builder().password(newPassword).user(found).build());
        found.editPassword(newPassword);

        // 비밀번호 이력이 3개를 넘는가?
        List<UserPassword> userPasswords = userPasswordRepository.findAllByUser_IdOrderByCreatedAt(
            found.getId());
        if (userPasswords.size() >= 3) {
            userPasswordRepository.deleteById(userPasswords.get(0).getId());
        }

        return found;
    }

    @Transactional
    public void editIcon(MultipartFile multipartFile, User user) throws IOException {
        try {
            User target = findUser(user.getId());
            String icon = s3Uploader.upload(multipartFile, "Icon");
            target.editIcon(icon);
        } catch (RejectedExecutionException e) {
            throw new CustomException(CustomErrorCode.S3_FILE_UPLOAD_FAIL);
        }
    }

    public void defaultIcon(User user) {
        try {
            user.editIcon("https://twogether.s3.ap-northeast-2.amazonaws.com/Icon/faed91e3-e029-45ee-a407-8efdfb178fce.png");
            userRepository.save(user);
        } catch (RejectedExecutionException e) {
            throw new CustomException(CustomErrorCode.ICON_UPLOAD_FAIL);
        }
    }

    public void logoutUser(HttpServletRequest req, User user) {
        findUser(user.getId());

        String refreshToken = req.getHeader(JwtUtil.REFRESH_TOKEN_HEADER);

        if(refreshToken != null && redisRefreshToken.hasKey(refreshToken)) {
            redisRefreshToken.removeRefreshToken(refreshToken);
        } else {
            throw new CustomException(CustomErrorCode.REFRESH_TOKEN_NOT_EXISTS);
        }
    }

    @Transactional
    public void deleteUserInfo(Long id, User user) {
        User found = findUser(id);
        confirmUser(found, user);

        logicallyDelete(user);
        userRepository.deleteById(found.getId());
    }

    private User findUser(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
            new CustomException(CustomErrorCode.USER_NOT_FOUND));
    }

    private void confirmUser(User user1, User user2) {
        if (!Objects.equals(user1.getId(), user2.getId())
            && !user2.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new CustomException(CustomErrorCode.UNAUTHORIZED_REQUEST);
        }
    }

    private void checkPassword(String inputPassword, String userPassword) {
        if (!passwordEncoder.matches(inputPassword, userPassword)) {
            throw new CustomException(CustomErrorCode.PASSWORD_MISMATCHED);
        }
    }

    private void checkRecentPasswords(Long userId, String newPassword) {
        List<UserPassword> userPasswords = userPasswordRepository.findAllByUser_IdOrderByCreatedAt(
            userId);
        userPasswords.forEach(password -> {
            if (passwordEncoder.matches(newPassword, password.getPassword())) {
                throw new CustomException(CustomErrorCode.PASSWORD_RECENTLY_USED);
            }
        });
    }

    private void logicallyDelete(User user) {
        wpRepository.findAllByUser_Id(user.getId()).forEach(
            workspace -> {
                boardRepository.findAllByWorkspace_Id(workspace.getId()).forEach(
                    board -> {
                        deckRepository.findAllByBoard_Id(board.getId()).forEach(
                            deck -> {
                                cardRepository.findAllByDeck_Id(deck.getId()).forEach(
                                    card -> {
                                        commentRepository.deleteAllByCard_Id(card.getId());
                                        cardLabelRepository.deleteAllByCard_Id(card.getId());
                                        checkListRepository.findAllByCardId(card.getId()).forEach(
                                            checkList -> chlItemRepository.deleteAllByCheckList_Id(
                                                checkList.getId())
                                        );
                                        checkListRepository.deleteAllByCard_Id(card.getId());
                                        cardRepository.delete(card);
                                    }
                                );
                                deckRepository.delete(deck);
                            }
                        );
                        boardColRepository.deleteAllByBoard_Id(board.getId());
                        boardRepository.delete(board);
                    }
                );
                wpColRepository.deleteAllByWorkspace_Id(workspace.getId());
                wpRepository.delete(workspace);
            }
        );
        userPasswordRepository.deleteAllByUser_Id(user.getId());
    }
}