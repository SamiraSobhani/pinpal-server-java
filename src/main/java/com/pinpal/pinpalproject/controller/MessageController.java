package com.pinpal.pinpalproject.controller;

import com.pinpal.pinpalproject.model.Poster;
import com.pinpal.pinpalproject.model.PosterMessage;
import com.pinpal.pinpalproject.model.User;
import com.pinpal.pinpalproject.payload.MessageRequest;
import com.pinpal.pinpalproject.repository.PosterMessageRepository;
import com.pinpal.pinpalproject.repository.PosterRepository;
import com.pinpal.pinpalproject.repository.UserRepository;
import com.pinpal.pinpalproject.security.UserPrincipal;
import com.pinpal.pinpalproject.service.EmailServiceImpl;
import com.pinpal.pinpalproject.service.PosterMessageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final PosterMessageServiceImpl posterMessageService;
    private final PosterRepository posterRepository;
    private final PosterMessageRepository posterMessageRepository;
    private final EmailServiceImpl emailService;
    private final UserRepository userRepository;

    @GetMapping("/messages")
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public ResponseEntity<Collection<PosterMessage>> deletePoster(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                  @RequestParam Long posterId) {
        Collection<PosterMessage> allMessages = posterMessageService.getAllMessages(posterId);
        return ResponseEntity.ok(allMessages);
    }

    @PostMapping("/message")
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public ResponseEntity<PosterMessage> addPoster(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                   @RequestBody MessageRequest messageRequest) {

        Optional<Poster> optionalPoster = posterRepository.findById(messageRequest.getPosterId());
        if (!optionalPoster.isPresent()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        if (!messageRequest.getInReplyToMessageId().equals(-1L)) {
            Optional<PosterMessage> optionalPosterMessage = posterMessageRepository.findById(messageRequest.getInReplyToMessageId());
            if (!optionalPosterMessage.isPresent()) {
                return ResponseEntity.unprocessableEntity().build();
            }
        }

        PosterMessage posterMessage = new PosterMessage();
        posterMessage.setPosterId(messageRequest.getPosterId());
        posterMessage.setInReplyToMessageId(messageRequest.getInReplyToMessageId());
        posterMessage.setContent(messageRequest.getContent());
        posterMessage.setSenderId(userPrincipal.getId());
        posterMessage.setTimestamp(System.currentTimeMillis());
        posterMessage.setUserImage(userPrincipal.getImageURL());
        posterMessage.setUserName(userPrincipal.getUsername());

        if (!userPrincipal.getId().equals(optionalPoster.get().getUser().getId())) {
            try {
                String message = "<i>Greetings!</i><br>";
                message += "<b>Wish you a nice day!</b><br>";
                message += "<font color=red>Duke</font>";

                Optional<User> owner = userRepository.findById(optionalPoster.get().getUser().getId());
                emailService.sendmail(owner.get().getEmail(), "PinPal Market - You Got a Message",
                        message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.ok(posterMessageRepository.save(posterMessage));
    }
}
