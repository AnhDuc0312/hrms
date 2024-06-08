package com.hrms.sys.services.chat;

import com.hrms.sys.exceptions.ChatAlreadyExistException;
import com.hrms.sys.exceptions.ChatNotFoundException;
import com.hrms.sys.exceptions.NoChatExistsInTheRepository;
import com.hrms.sys.models.Chat;
import com.hrms.sys.models.Message;

import java.util.HashSet;
import java.util.List;

public interface IChatService {
    public Chat addChat(Chat chat) throws ChatAlreadyExistException;

    List<Message> getAllMessagesInChat(Long chatId) throws NoChatExistsInTheRepository;

    List<Chat> findallchats() throws NoChatExistsInTheRepository;



    Chat getById(Long id) throws ChatNotFoundException;

    HashSet<Chat> getChatByFirstUserName(String username)  throws ChatNotFoundException;

    HashSet<Chat> getChatBySecondUserName(String username)  throws ChatNotFoundException;

    HashSet<Chat> getChatByFirstUserNameOrSecondUserName(String username)  throws ChatNotFoundException;

    HashSet<Chat> getChatByFirstUserNameAndSecondUserName(String firstUserName, String secondUserName)  throws ChatNotFoundException;

    Message addMessage2(Message message);

    Chat addMessage(Message add, Long chatId) throws ChatNotFoundException;
}
