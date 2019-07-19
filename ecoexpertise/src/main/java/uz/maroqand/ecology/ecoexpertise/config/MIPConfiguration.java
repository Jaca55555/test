package uz.maroqand.ecology.ecoexpertise.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import uz.maroqand.ecology.ecoexpertise.mips.*;


@Configuration
public class MIPConfiguration {

    /*@Bean(name = "mipLegalEntityInfoServiceMarshaller")
    public Jaxb2Marshaller mipLegalEntityInfoServiceMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("z.mips.le_info_by_tin");
        return marshaller;
    }

    @Bean(name = "mipIndividualsPassportInfoService")
    public Jaxb2Marshaller mipIndividualsPassportInfoService() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("z.mips.i_passport_info");
        return marshaller;
    }*/

    public Jaxb2Marshaller getMarshallerForContextPath(String path) {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(path);
        return marshaller;
    }



    @Bean
    public MIPLegalEntityInfoService MIPLegalEntityInfoService() {
        Jaxb2Marshaller marshaller = getMarshallerForContextPath("uz.maroqand.ecology.ecoexpertise.mips.le_info_by_tin");

        MIPLegalEntityInfoService client = new MIPLegalEntityInfoService();

        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

    @Bean
    public MIPIndividualsPassportInfoService MIPIndividualsPassportInfoService() {
        Jaxb2Marshaller marshaller = getMarshallerForContextPath("uz.maroqand.ecology.ecoexpertise.mips.i_passport_info");

        MIPIndividualsPassportInfoService client = new MIPIndividualsPassportInfoService();

        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;

    }

    @Bean
    public MIPIndividualsAddressInfoService MIPIndividualsAddressInfoService() {
        Jaxb2Marshaller marshaller = getMarshallerForContextPath("uz.maroqand.ecology.ecoexpertise.mips.i_address_info");

        MIPIndividualsAddressInfoService client = new MIPIndividualsAddressInfoService();

        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

    @Bean
    public MIPIndividualsTaxInfoByTINService MIPIndividualsTaxInfoByTINService() {
        Jaxb2Marshaller marshaller = getMarshallerForContextPath("uz.maroqand.ecology.ecoexpertise.mips.i_tax_info_by_tin");

        MIPIndividualsTaxInfoByTINService client = new MIPIndividualsTaxInfoByTINService();

        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }


    @Bean
    public MIPIndividualsTaxInfoByPINFLService MIPIndividualsTaxInfoByPINFLService() {
        Jaxb2Marshaller marshaller = getMarshallerForContextPath("uz.maroqand.ecology.ecoexpertise.mips.i_tax_info_by_pin");

        MIPIndividualsTaxInfoByPINFLService client = new MIPIndividualsTaxInfoByPINFLService();

        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

}
