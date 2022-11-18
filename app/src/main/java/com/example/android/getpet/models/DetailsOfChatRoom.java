package com.example.android.getpet.models;

//Contains information about the sender and receiver.
public class  DetailsOfChatRoom {

    private String PetKey;
    private String ReceiverKey;
    private String ReceiverName;
    private String ReceiverEmail;
    private String ReceiverProfilePic;
    private String ChatRoomName;
    private String SenderKey;
    private String SenderName;
    private String SenderEmail;
    private String SenderProfilePic;
    private String PetName;

    public DetailsOfChatRoom(String petKey, String receiverKey, String receiverName, String receiverEmail, String receiverProfilePic, String chatRoomName, String senderKey, String senderName, String senderEmail, String senderProfilePic, String petName) {
        PetKey = petKey;
        ReceiverKey = receiverKey;
        ReceiverName = receiverName;
        ReceiverEmail = receiverEmail;
        ReceiverProfilePic = receiverProfilePic;
        ChatRoomName = chatRoomName;
        SenderKey = senderKey;
        SenderName = senderName;
        SenderEmail = senderEmail;
        SenderProfilePic = senderProfilePic;
        PetName = petName;
    }

    public DetailsOfChatRoom() {
    }

    public String getPetName() {
        return PetName;
    }

    public void setPetName(String petName) {
        PetName = petName;
    }

    public String getPetKey() {
        return PetKey;
    }

    public void setPetKey(String petKey) {
        PetKey = petKey;
    }

    public String getReceiverKey() {
        return ReceiverKey;
    }

    public void setReceiverKey(String receiverKey) {
        ReceiverKey = receiverKey;
    }

    public String getReceiverName() {
        return ReceiverName;
    }

    public void setReceiverName(String receiverName) {
        ReceiverName = receiverName;
    }

    public String getReceiverEmail() {
        return ReceiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        ReceiverEmail = receiverEmail;
    }

    public String getReceiverProfilePic() {
        return ReceiverProfilePic;
    }

    public void setReceiverProfilePic(String receiverProfilePic) {
        ReceiverProfilePic = receiverProfilePic;
    }

    public String getChatRoomName() {
        return ChatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        ChatRoomName = chatRoomName;
    }

    public String getSenderKey() {
        return SenderKey;
    }

    public void setSenderKey(String senderKey) {
        SenderKey = senderKey;
    }

    public String getSenderName() {
        return SenderName;
    }

    public void setSenderName(String senderName) {
        SenderName = senderName;
    }

    public String getSenderEmail() {
        return SenderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        SenderEmail = senderEmail;
    }

    public String getSenderProfilePic() {
        return SenderProfilePic;
    }

    public void setSenderProfilePic(String senderProfilePic) {
        SenderProfilePic = senderProfilePic;
    }
}
