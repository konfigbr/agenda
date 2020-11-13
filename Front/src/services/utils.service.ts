import { Injectable } from '@angular/core';

import { format } from 'date-fns'
import ptBrLocale from 'date-fns/locale/pt-BR'

@Injectable({
    providedIn: 'root'
})
export class UtilsService {

    public currencyFormat(ammount: any) {
        if (ammount) {
            ammount = ammount.toString();
            ammount =
                ammount && ammount.indexOf(".") !== -1 && ammount.indexOf(",") !== -1
                    ? this.currencyClear(ammount)
                    : ammount;
            if (parseFloat(ammount) * 100 > parseInt(ammount) * 100) {
                ammount = Math.floor(parseFloat(ammount) * 100);
            }
            ammount = ammount && ammount.toString();

            ammount = ammount && ammount.replace(/\D/g, "");
            ammount = ammount && parseInt(ammount);
            return (
                ammount &&
                parseFloat((ammount / 100).toString())
                    .toFixed(2)
                    .replace(".", ",")
                    .replace(/(\d)(?=(\d{3})+(?!\d))/g, "$1.")
            );
        } else {
            return ""
        }
    }
    public currencyFormatNumber(ammount: string) {
        if (ammount) {
            ammount = ammount.replace(".", "");
            ammount = ammount.replace(".", "");
            ammount = ammount.replace(".", "");
            ammount = ammount.replace(".", "");
            ammount = ammount.replace(".", "");
            ammount = ammount.replace(".", "");
            ammount = ammount.replace(".", "");
            ammount = ammount.replace(".", "");
            ammount = ammount.replace(".", "");
            ammount = ammount.replace(".", "");
            return ammount && ammount.replace(",", ".");
        }
    }
    private currencyClear(ammount: string) {
        if (ammount) {
            ammount = ammount.replace(".", "");
            ammount = ammount.replace(".", "");
            ammount = ammount.replace(".", "");
            ammount = ammount.replace(".", "");
            ammount = ammount.replace(".", "");
            ammount = ammount.replace(".", "");
            ammount = ammount.replace(".", "");
            ammount = ammount.replace(".", "");
            ammount = ammount.replace(".", "");
            ammount = ammount.replace(".", "");
            return ammount && ammount.replace(",", "");
        }
    }
    public mCEP(cep: string) {
        if (cep) {
            cep = cep.replace(/\D/g, "");
            cep = cep.replace(/(\d{2})(\d)/, "$1.$2");
            cep = cep.replace(/(\d{3})(\d{3})$/, "$1-$2");
            return cep;
        }
    }
    public mCEPOut(cep: string) {
        if (cep) {
            cep = cep.replace(".", "");
            cep = cep.replace("-", "");
            return cep;
        }
    }
    public mCPF(cpf: string) {
        if (cpf) {
            cpf = cpf.replace(/\D/g, "");
            cpf = cpf.replace(/(\d{3})(\d)/, "$1.$2");
            cpf = cpf.replace(/(\d{3})(\d)/, "$1.$2");
            cpf = cpf.replace(/(\d{3})(\d{1,2})$/, "$1-$2");
            return cpf;
        }
    }
    public mJustLetter(text: any) {
        if (text) {
            return text.match(/[A-Za-zÀ-ÿ  .\-]+/g).join("");
        }
    }
    public mCPFOut(cpf: string) {
        if (cpf) {
            cpf = cpf.replace(".", "");
            cpf = cpf.replace(".", "");
            cpf = cpf.replace("-", "");
            return cpf;
        }
    }
    public validateName(name: string) {
        if (name) {
            return (
                name.split(" ")[0] &&
                name.split(" ")[0].length >= 2 &&
                name.split(" ")[1] &&
                name.split(" ")[1].length >= 2
            );
        }
    }
    public mPhone(phone: string) {
        if (phone) {
            phone = phone.replace(/\D/g, "");
            phone = phone.replace(/(\d{0})(\d)/, "$1($2");
            phone = phone.replace(/(\d{2})(\d)/, "$1) $2");

            this.mPhoneOut(phone).length < 11
                ? (phone = phone.replace(/(\d{4})(\d)/, "$1-$2"))
                : (phone = phone.replace(/(\d{5})(\d)/, "$1-$2"));
            return phone;
        }
    }

    public mPhoneOut(phone: string): string {
        if (phone) {
            phone = phone.replace("(", "");
            phone = phone.replace(")", "");
            phone = phone.replace(" ", "");
            phone = phone.replace("-", "");
            return phone;
        } else {
            return "";
        }
    }
    public validateStrongPassword(password: any) {
        let capitalLetters = 0;
        let lowerCaseLeters = 0;
        let numbers = 0;
        let specialCaracters = 0;
        const specialCaractersList = "/([~`!@#$%^&*+=-[]\\';,/{}|\":<>?])";

        for (let i = 0; i <= password.length; i++) {
            const asciiValue = password.charCodeAt(i);
            if (asciiValue >= 65 && asciiValue <= 90) {
                capitalLetters++;
            }
            if (asciiValue >= 97 && asciiValue <= 122) {
                lowerCaseLeters++;
            }
            if (asciiValue >= 48 && asciiValue <= 57) {
                numbers++;
            }
            if (specialCaractersList.indexOf(password[i]) !== -1) {
                specialCaracters++;
            }
        }

        if (
            capitalLetters &&
            lowerCaseLeters &&
            numbers &&
            specialCaracters &&
            password.length >= 8
        ) {
            return true;
        } else {
            return false;
        }
    }
    public validateEmail(email: string) {
        const regexValidate = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return regexValidate.test(String(email).toLowerCase());
    }

    public validaCPF(strCPF:string):boolean {
        let i;
        let Soma;
        let Resto;
        const cpfsInvalidos = [
            '00000000000',
            '11111111111',
            '22222222222',
            '33333333333',
            '44444444444',
            '55555555555',
            '66666666666',
            '77777777777',
            '88888888888',
            '99999999999',
        ];

        if (cpfsInvalidos.includes(strCPF)) {
            return false;
        }

        Soma = 0;

        for (i=1; i<=9; i++) Soma = Soma + parseInt(strCPF.substring(i-1, i)) * (11 - i);
        Resto = (Soma * 10) % 11;

        if ((Resto == 10) || (Resto == 11))  Resto = 0;
        if (Resto != parseInt(strCPF.substring(9, 10)) ) return false;

        Soma = 0;
        for (i = 1; i <= 10; i++) Soma = Soma + parseInt(strCPF.substring(i-1, i)) * (12 - i);
        Resto = (Soma * 10) % 11;

        if ((Resto == 10) || (Resto == 11))  Resto = 0;
        if (Resto != parseInt(strCPF.substring(10, 11) ) ) return false;
        return true;
    }


    validaCNPJ(cnpj) {

        
        // Verifica se a variável cnpj é igua a "undefined", exibindo uma msg de erro
        if (cnpj === undefined) {
          
          return false;
        }
    
        // Esta função retira os caracteres . / - da string do cnpj, deixando apenas os números 
        var strCNPJ = cnpj.replace('.', '').replace('.', '').replace('/', '').replace('-', '');
    
        // Testa as sequencias que possuem todos os dígitos iguais e se o cnpj não tem 14 dígitos, retonando falso e exibindo uma msg de erro
        if (strCNPJ === '00000000000000' || strCNPJ === '11111111111111' || strCNPJ === '22222222222222' || strCNPJ === '33333333333333' ||
          strCNPJ === '44444444444444' || strCNPJ === '55555555555555' || strCNPJ === '66666666666666' || strCNPJ === '77777777777777' ||
          strCNPJ === '88888888888888' || strCNPJ === '99999999999999' || strCNPJ.length !== 14) {
          return false;
        }
    
        // A variável numeros pega o bloco com os números sem o DV, a variavel digitos pega apenas os dois ultimos numeros (Digito Verificador).
        var tamanho = strCNPJ.length - 2;
        var numeros = strCNPJ.substring(0, tamanho);
        var digitos = strCNPJ.substring(tamanho);
        var soma = 0;
        var pos = tamanho - 7;
    
        // Os quatro blocos seguintes de funções irá reaizar a validação do CNPJ propriamente dito, conferindo se o DV bate. Caso alguma das funções não consiga verificar
        // o DV corretamente, mostrará uma mensagem de erro ao usuário e retornará falso, para que o usário posso digitar novamente um número 
        for (let i = tamanho; i >= 1; i--) {
          soma += numeros.charAt(tamanho - i) * pos--;
          if (pos < 2) {
            pos = 9;
          }
        }
    
        var resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;
        if (resultado != digitos.charAt(0)) {
          
          return false;
        }
    
        tamanho = tamanho + 1;
        numeros = strCNPJ.substring(0, tamanho);
        soma = 0;
        pos = tamanho - 7;
        for (let k = tamanho; k >= 1; k--) {
          soma += numeros.charAt(tamanho - k) * pos--;
          if (pos < 2) {
            pos = 9;
          }
        }
    
        resultado = soma % 11 < 2 ? 0 : 11 - soma % 11;
        if (resultado != digitos.charAt(1)) {
          
          return false;
        }
    
        
        return true;
      }
    
    formatDate(date: string, pattern: string): string {
        return format(new Date(date), pattern, {
            locale: ptBrLocale,
        })
    }
}
