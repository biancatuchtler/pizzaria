import java.util.List;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramBotAdapter;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ChatAction;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetFileResponse;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;

//acessar o bot father
//crir um novo bot
//pegar o token dado pelo bot father e inserir na linha 22

public class Bot {

	public static void main(String[] args) throws InterruptedException {

		// Criacao do objeto bot com as informacoes de acesso
		TelegramBot bot = TelegramBotAdapter.build("659501081:AAGAKmJBZ7M0s3VR2dIgOvlpl5sBKYa6y3U\n");

		// objeto responsavel por receber as mensagens
		GetUpdatesResponse updatesResponse;
		// objeto responsavel por gerenciar o envio de respostas
		SendResponse sendResponse = null;
		// objeto responsavel por gerenciar o envio de a��es do chat
		BaseResponse baseResponse;

		// controle de off-set. A partir deste ID serao lidas as mensagens
		// pendentes na fila
		int m = 0;

		// loop infinito pode ser alterado por algum timer de intervalo curto
		while (true) {

			// executa comando no Telegram para obter as mensagens pendentes a partir de um
			// off-set (limite inicial)
			updatesResponse = bot.execute(new GetUpdates().limit(100).offset(m));

			// lista de mensagens
			List<Update> updates = updatesResponse.updates();

			// an�lise de cada acao da mensagem
			for (Update update : updates) {

				// atualizacao do off-set
				m = update.updateId() + 1;

				if (update.callbackQuery() != null) {

					sendResponse = bot.execute(new SendMessage(update.callbackQuery().message().chat().id(),
							update.callbackQuery().data()));
				} else {
					System.out.println("Recebendo mensagem:" + update.message().text());

					// envio de "Escrevendo" antes de enviar a resposta
					baseResponse = bot
							.execute(new SendChatAction(update.message().chat().id(), ChatAction.typing.name()));
					// verificacao de acao de chat foi enviada com sucesso
					System.out.println("Resposta de Chat Action Enviada?" + baseResponse.isOk());

					// enviando numero de contato recebido
					if (update.message().contact() != null) {
						sendResponse = bot.execute(new SendMessage(update.message().chat().id(),
								"Número " + update.message().contact().phoneNumber() + " enviado"));
					} else if (update.message().location() != null) {
						sendResponse = bot.execute(new SendMessage(update.message().chat().id(),
								"Latitude " + update.message().location().latitude() + " enviada"));
					}

					if (update.message().text() != null) {
						switch(update.message().text()) {
						case "/start": {
							sendResponse = bot.execute(new SendMessage(update.message().chat()
									.id(), "Bem vindo � pizzaria topper, voc� deseja iniciar um pedido ou alterar seu cadastro?").replyMarkup(
									new ReplyKeyboardMarkup(new String[] { "Iniciar pedido", "Alterar cadastro" })));
							break;
						}
						case "Alterar cadastro":{
							sendResponse = bot.execute(new SendMessage(update.message().chat()
									.id(), "Inserindo keyboard").replyMarkup(
									new ReplyKeyboardMarkup(new String[] { "Iniciar pedido", "Alterar cadastro" })));
							break;
						}
						case "/limparkeyboard": {
							sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "limpando keyboard")
									.replyMarkup(new ReplyKeyboardRemove()));
							break;

						}
						case "/pedircontato":{
							sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "pedindo contato")
								.replyMarkup(new ReplyKeyboardMarkup(new KeyboardButton[] {
								new KeyboardButton("Fornecer contato").requestContact(true) })));
							break;
						}
						case "/pedirlocalizacao":{
							sendResponse = bot
									.execute(new SendMessage(update.message().chat().id(), "pedindo localização")
									.replyMarkup(new ReplyKeyboardMarkup(
									new KeyboardButton[] { new KeyboardButton("Fornecer localização")
									.requestLocation(true) })));
							break;
						}
						case "/keyboardInlineUrl":{
							sendResponse = bot.execute(new SendMessage(update.message().chat().id(), "Inserindo keyboard inline")
											.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[] { new InlineKeyboardButton("url").url("http://www.google.com.br") })));
						}
						case "/keyboardInlineCallBack":{
							sendResponse = bot
									.execute(new SendMessage(update.message().chat().id(), "Inserindo keyboard inline")
											.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton[] {
													new InlineKeyboardButton("Texto de apresentação").callbackData("Texto enviado pelo callback")})));

						}
						case "default":{
							sendResponse = bot.execute(new SendMessage(update.message().chat().id(),
									"Digite uma das seguintes opções:" +"\n /start"
											+ "\n /limparkeyboard"
											+ "\n /pedircontato"
											+ "\n /pedirlocalizacao"
											+ "\n /keyboardInlineUrl"
											+ "\n /keyboardInlineCallBack"
											+ "\n /updateNormalMessage"));
						}
						}
						System.out.println("Mensagem Enviada?" + sendResponse.isOk());
					}
				}
			}
		}
	}
}
