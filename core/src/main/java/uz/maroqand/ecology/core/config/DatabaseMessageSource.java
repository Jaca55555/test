package uz.maroqand.ecology.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.stereotype.Component;
import uz.maroqand.ecology.core.entity.sys.Translation;
import uz.maroqand.ecology.core.service.sys.TranslationService;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * Created by Utkirbek Boltaev on 10.06.2019.
 * (uz)
 */
@Component
public class DatabaseMessageSource extends AbstractMessageSource {

    private final TranslationService translationService;

//    private static final Logger logger = LogManager.getLogger(DatabaseMessageSource.class);

    @Autowired
    public DatabaseMessageSource(TranslationService translationService) {
        this.translationService = translationService;
    }


    @Override
    protected MessageFormat resolveCode(String tag, Locale locale) {
        logger.info("resolveCode '"+tag+"'");
        Translation translation = translationService.findByName(tag);
        if(translation!=null) {
            return new MessageFormat(translation.getNameTranslation(locale+""));
        }
        return null;
    }

    @Override
    public String resolveCodeWithoutArguments(String tag, Locale locale) {
        Translation translation = translationService.findByName(tag);
        if(translation!=null) {
            return translation.getNameTranslation(locale.getLanguage());
        }
        return null;
    }

    public String resolveCodeSimply(String tag, String locale) {
        Translation translation = translationService.findByName(tag);
        if(translation!=null) {
            return translation.getNameTranslation(locale);
        }
        return "";
    }

}
