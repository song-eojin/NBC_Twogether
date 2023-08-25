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
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserPasswordRepository userPasswordRepository;
    private final WpRepository wpRepository;
    private final BoardRepository boardRepository;
    private final DeckRepository deckRepository;
    private final CardLabelRepository cardLabelRepository;
    private final CommentRepository commentRepository;
    private final CheckListRepository checkListRepository;
    private final ChlItemRepository chlItemRepository;
    private final CardRepository cardRepository;
    private final BoardColRepository boardColRepository;
    private final WpColRepository wpColRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.token}")
    private String adminToken;

    @Transactional
    public User signup(SignupRequestDto requestDto) {
        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 사용자 존재 여부 확인
        findExistingUserByEmail(email);

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
    public void deleteUserInfo(Long id, User user) {
        User found = findUser(id);
        confirmUser(found, user);

        wpRepository.findAllByUser_Id(id).forEach(
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
                                            checkList -> chlItemRepository.deleteAllByCheckList_Id(checkList.getId())
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
        userPasswordRepository.deleteAllByUser_Id(found.getId());
        userRepository.deleteById(found.getId());
    }

    @Transactional
    public User editUserPassword(EditPasswordRequestDto requestDto, User user) {
        User found = findUser(user.getId());

        checkPassword(requestDto.getPassword(), found.getPassword());       // 기존 비밀번호 일치 여부 확인
        checkRecentPasswords(found.getId(), requestDto.getNewPassword());   // 바로 직전 혹은 기존에 사용 중인 비밀번호인지 확인

        // 새 비밀번호 저장
        String newPassword = passwordEncoder.encode(requestDto.getNewPassword());
        userPasswordRepository.save(UserPassword.builder().password(newPassword).user(found).build());
        found.editPassword(newPassword);

        // 비밀번호 이력이 3개를 넘는가?
        List<UserPassword> userPasswords = userPasswordRepository.findAllByUser_IdOrderByCreatedAt(found.getId());
        if(userPasswords.size() >= 3)
            userPasswordRepository.deleteById(userPasswords.get(0).getId());

        return found;
    }

    private void findExistingUserByEmail(String email) {
        if (userRepository.findByEmail(email).orElse(null) != null) {
            throw new CustomException(CustomErrorCode.USER_ALREADY_EXISTS);
        }
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
        List<UserPassword> userPasswords = userPasswordRepository.findAllByUser_IdOrderByCreatedAt(userId);
        userPasswords.forEach(password -> {
            if (passwordEncoder.matches(newPassword, password.getPassword()))
                throw new CustomException(CustomErrorCode.PASSWORD_RECENTLY_USED);
        });
    }
}