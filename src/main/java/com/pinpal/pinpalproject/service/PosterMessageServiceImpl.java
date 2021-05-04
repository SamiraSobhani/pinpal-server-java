package com.pinpal.pinpalproject.service;

import com.pinpal.pinpalproject.model.PosterMessage;
import com.pinpal.pinpalproject.repository.PosterMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PosterMessageServiceImpl {

    private final PosterMessageRepository posterMessageRepository;

    public Collection<PosterMessage> getAllMessages(Long posterId) {

        Map<Long, PosterMessage> resultMap = new LinkedHashMap<>();
        Optional<List<PosterMessage>> posterMessagesOptional = posterMessageRepository.findAllByPosterIdOrderByTimestampAsc(posterId);

        if (!posterMessagesOptional.isPresent()) {
            return Collections.emptyList();
        }

        List<PosterMessage> posterMessages = posterMessagesOptional.get();
        for (PosterMessage posterMessage : posterMessages) {
            if (posterMessage.getInReplyToMessageId() < 0) {
                // parent message - add it to the linked map
                resultMap.put(posterMessage.getId(), posterMessage);
            } else {
                Long parentId = posterMessage.getInReplyToMessageId();
                PosterMessage parentMessage = resultMap.get(parentId);
                List<PosterMessage> parentNestedReplies = parentMessage.getNestedReplies();
                parentNestedReplies.add(posterMessage);
                parentMessage.setNestedReplies(parentNestedReplies);
                resultMap.put(parentId, parentMessage);
            }
        }

        return resultMap.values();
    }
}
