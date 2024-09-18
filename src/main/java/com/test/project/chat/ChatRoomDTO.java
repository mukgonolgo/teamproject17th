package com.test.project.chat;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Setter
@AllArgsConstructor
@Builder
public class ChatRoomDTO {

    private String roomId;
    private String roomName;
    private int currentMembers;
    private int maxMembers;
    private List<MemberDTO> memberNames;
}