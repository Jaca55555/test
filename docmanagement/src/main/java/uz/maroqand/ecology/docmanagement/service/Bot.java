package uz.maroqand.ecology.docmanagement.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.maroqand.ecology.core.constant.telegram.SendQueryType;
import uz.maroqand.ecology.core.entity.user.User;
import uz.maroqand.ecology.core.service.user.UserService;
import uz.maroqand.ecology.docmanagement.service.interfaces.DocumentTaskSubService;

import java.util.ArrayList;
import java.util.List;

@Service
public class Bot extends TelegramLongPollingBot {
    private static final String botUserName="test_doc_managment_bot";
    private static final String botToken="1215527316:AAHQYyK8_id-BHsHjDAAyKWxw5KgYgAP0dU";
    private static final String Title_all="Menu orqali xoxlagan bandni tanlash \n orqali kerakli malumot olishingiz mumkin";
    private static final String ALL_DOCUMENT="HAMMASI";
    private static final String NEW_DOCUMENT="YAGI";
    private static final String IN_PROCESS_DOCUMENT="JARAYONDAGI";
    private static final String DUE_DATE_DOCUMENT="MUDDATI_YAQINLASHGAN";
    private static final String EXECUTIVE_DOCUMENT="MUDDATI_TUGAGAN";

    private final UserService userService;
    private final DocumentTaskSubService  documentTaskSubService;

    public Bot(UserService userService,DocumentTaskSubService  documentTaskSubService) {
        this.userService = userService;
        this.documentTaskSubService = documentTaskSubService;
    }

    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText() ) {
            returnMessage(update);
        }
    }

    public String getBotUsername() {
        return botUserName;
    }

    public String getBotToken() {
        return botToken;
    }

    public void returnMessage(Update update) {

        Message message = update.getMessage();
        Integer chatId = message.getFrom().getId();
        switch (message.getText()){
            case "/start":
                loginDocManagment(message); break;
            case ALL_DOCUMENT:
                sendMsg(chatId,documentTaskSubService.getMessageText(chatId, SendQueryType.All));break;
            case NEW_DOCUMENT:
                sendMsg(chatId,documentTaskSubService.getMessageText(chatId, SendQueryType.NewDocument));break;
            case IN_PROCESS_DOCUMENT:
                sendMsg(chatId,documentTaskSubService.getMessageText(chatId, SendQueryType.InProccess));break;
            case DUE_DATE_DOCUMENT:
                sendMsg(chatId,documentTaskSubService.getMessageText(chatId, SendQueryType.DueDate));break;
            case EXECUTIVE_DOCUMENT:
                sendMsg(chatId,documentTaskSubService.getMessageText(chatId, SendQueryType.ExecutiveDate));break;
            default: registration(message);
        }
    }

    public void sendMsg(Integer chatId, String title){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(title.trim());
        try {
            Boolean isReg =userService.isRegistrationUser(chatId);
            if (isReg){
                setButtons(sendMessage);
            }
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void registration(Message message){
        if (userService.isRegistrationUser(message.getFrom().getId())){
            sendMsg(message.getFrom().getId(),Title_all);
            return;
        }
        String [] text = message.getText().split(" ");
        System.out.println(text.length);
        if (text.length==2){
             User user = userService.login(text[0],text[1]);
             if (user!=null){
                 user.setTelegramUserId(message.getFrom().getId());
                 userService.updateUser(user);
                 String userFullName = user.getFullName()+"  \n Siz botdan ro'yxardan o'tdingiz: \n O'zingizga kerakli tugma orqali malumot olishingiz mumkin";
                 sendMsg(message.getFrom().getId(),userFullName);
                 sendMsg(message.getFrom().getId(),Title_all);
                 return;

             }
        }
        sendMsg(message.getFrom().getId(),"Siz login yoki parolni notog'ri yozdingiz. \n iltimos qatadan urinib ko'ring \n `Izox!! login va parol orasida bitta bo'sh joy qolishi kerak`");
        sendMsg(message.getFrom().getId(),Title_all);
        return;
    }

    public void loginDocManagment(Message message){
        if (userService.isRegistrationUser(message.getFrom().getId())){
            sendMsg(message.getFrom().getId(),Title_all);
            return;
        }
        String loginPass = "Xush kelibsiz!! \n  Siz botdan foydalanishingiz uchun login parolingizni kiriting: \n  " ;
        sendMsg(message.getFrom().getId(),loginPass);
        loginPass = "login: password:" ;
        sendMsg(message.getFrom().getId(),loginPass);
    }

    public void setButtons(SendMessage sendMessage){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton(ALL_DOCUMENT));
        keyboardRowList.add(keyboardRow);
        keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton(NEW_DOCUMENT));
        keyboardRow.add(new KeyboardButton(IN_PROCESS_DOCUMENT));
        keyboardRowList.add(keyboardRow);
        keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton(DUE_DATE_DOCUMENT));
        keyboardRow.add(new KeyboardButton(EXECUTIVE_DOCUMENT));
        keyboardRowList.add(keyboardRow);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

}
