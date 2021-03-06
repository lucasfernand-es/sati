/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Model.EdicaoModel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import valueObject.Edicao;

/**
 *
 * @author Lucas
 */
public class EdicaoController {
    
    private final EdicaoModel edicaoModel = new EdicaoModel();
    
    public enum Verifica {
        CADASTRAR, ALTERAR
    }

    // Fun��o verifica os dados do objeto. Se sim, retorna true, se n�o retorna false
    // Altera a mensagem do objeto para especificar os erros
    private boolean verificarAtributos(Edicao edicao, Verifica verifica) {
        

        // Verifica campos vazios
        if(     edicao.getTema() == null        ||  edicao.getTitulo() == null || 
                edicao.getTema().equals("")     ||  edicao.getTitulo().equals("") || 
                edicao.getDataInicio() == null  ||  edicao.getDataFim() == null ||
                edicao.getDadosBancarios() == null)
            return false;
        else {
            
            /*Date hoje = edicao.getDataInicio(); 
            
            Calendar cal = new GregorianCalendar();
            cal.setTime(hoje);
            // Verifica se � 30 dias antes
            cal.add(Calendar.DAY_OF_MONTH, +29);
            
            hoje = cal.getTime();*/
                
            if(     !edicao.getDataInicio().before( edicao.getDataFim() )/* ||
                    edicao.getDataInicio().after( hoje )*/
                    )
                return false;
            
            if( verifica == Verifica.CADASTRAR ){
                if( this.buscarEdicaoExistente(edicao, "ANO") )
                    return false;
            }
            
            return true;
            
        }
    }
    
    // Retorna verdadeiro se for poss�vel cadastrar
    public boolean cadastrarEdicao(Edicao edicao) {
        
        boolean verifica = this.verificarAtributos(edicao, Verifica.CADASTRAR);
        
        if(!verifica) {
            //edicao.setError(true);
            // Algum dado informado � inv�lido
            //System.out.println(edicao.getMessage());
            return false;
        }
       
        
        return edicaoModel.cadastrarEdicao(edicao);
        
    }
    
    public boolean alterarEdicao(Edicao edicao) {
        
        boolean verifica = this.verificarAtributos(edicao, Verifica.ALTERAR);
        
        if(!verifica) {
            edicao.setError(true);
            
            // Algum dado informado � inv�lido
            return false;
        }
        
        boolean res = edicaoModel.alterarEdicao(edicao);
        System.out.println(edicao.getMessage());
        return res;
    }
    
    public boolean buscarEdicaoExistente(Edicao edicao, String tipo) {
        
        ArrayList<Edicao> newList = this.buscarEdicao(edicao, tipo);
        return !(newList == null || newList.isEmpty());
        
    }
    
    public ArrayList<Edicao> buscarEdicao(Edicao edicao, String tipo) {
        
        ArrayList<Edicao> newList = this.edicaoModel.buscarEdicao(edicao, tipo);
        
        // Se n�o houve nenhum erro na pesquisa, e ainda assim, a lista est� vazia
        if(newList == null || edicao.isError())
        {
            edicao.setError(true);
            edicao.setMessage("Erro!");
            //System.out.println("ERRO");
            return null;
        }
        
        if( newList.isEmpty() ) {
           //System.out.println("Nenhum item foi encontrado!");
           edicao.setMessage("Nenhum item foi encontrado!"); 
        }
        
        return newList;
        
    }

    public boolean excluirEdicao(Edicao edicao, String tipo) {
        
        return edicaoModel.excluirEdicao(edicao, tipo);
    }
    
}