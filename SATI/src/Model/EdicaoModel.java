/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Connector.MySQLConnector;
import Controller.Util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import valueObject.Edicao;
import valueObject.Pessoa;

/**
 *
 * @author jao
 */
public class EdicaoModel{

    public boolean cadastrarEdicao(Edicao edicao){
        
        try { 
            // Connect with database
            MySQLConnector mCon = new MySQLConnector();
            Connection con = mCon.connect();

            String query = "INSERT INTO Edicao( dataInicio, dataFim, dataVencimentoInscricao, "
                    + "agendaDefinida, titulo, tema) values (?, ?, ?, ?, ?, ?)";
        
            PreparedStatement stm;
            stm = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            int cont = 1;
            
            Timestamp dataInicio = new Timestamp(edicao.getDataInicio().getTime());
            stm.setTimestamp(cont++, dataInicio);
            
            Timestamp dataFim = new Timestamp(edicao.getDataFim().getTime());
            stm.setTimestamp(cont++, dataFim);
            
            Timestamp dataVencimento = new Timestamp(edicao.getDataVencimentoInscricao().getTime());
            stm.setTimestamp(cont++, dataVencimento);
            
            //stm.setDate(cont++, (java.sql.Date) edicao.getDataInicio());
            //stm.setDate(cont++, (java.sql.Date) edicao.getDataFim());
            //stm.setDate(cont++, (java.sql.Date) edicao.getDataVencimentoInscricao());
            stm.setBoolean(cont++, edicao.isAgendaDefinida());
            stm.setString(cont++, edicao.getTitulo());
            stm.setString(cont++, edicao.getTema());
           
            int status = stm.executeUpdate();
            
            if(status == 1){
                edicao.setError(false);
                edicao.setMessage("Cadastrado com Sucesso!");
                
                ResultSet rs = stm.getGeneratedKeys();
                rs.next();
                
                int key = rs.getInt(1);
                //System.out.println("Key" + key);
                edicao.setIdEdicao(key);
            
            }
            else{
                edicao.setError(true);
                edicao.setMessage("Falha ao cadastrar nova Edi��o!");
                
            }
            mCon.disconnect();
            return !edicao.isError();
        } catch (Exception e) {
           edicao.setError(true);
           edicao.setMessage("Falha ao Cadastrar Edi��o\n\t" + e.getMessage());
           return false;
        }
    }
    public static void alterarEdicao(Edicao edicao){
        try { 
            
            MySQLConnector mCon = new MySQLConnector();
            Connection con = mCon.connect();
            
            String query = "update Edicao set dataInicio = ?,"
                    + "dataFim = ?, "
                    + "dataVencimentoInscricao = ?, "
                    + "agendaDefinida = ?, "
                    + "titulo = ?, "
                    + "tema = ?, "
                    + "idDadosBancarios = ?"
                    + "where idEdicao = ?";
            
            PreparedStatement stm;
            stm = con.prepareStatement(query);
            
            int cont = 1;
            
            //Alterando
            stm.setDate(cont++, (java.sql.Date) edicao.getDataInicio());
            stm.setDate(cont++, (java.sql.Date) edicao.getDataFim());
            stm.setDate(cont++, (java.sql.Date) edicao.getDataVencimentoInscricao());
            stm.setBoolean(cont++, edicao.isAgendaDefinida());
            stm.setString(cont++, edicao.getTitulo());
            stm.setString(cont++, edicao.getTema());
            stm.setInt(cont++, edicao.getDadosBancarios().getIdDadosBancarios());
            stm.setInt(cont++, edicao.getIdEdicao());
            
            int status =  stm.executeUpdate();
            
            if(status == 1){
                
                edicao.setError(false);
                edicao.setMessage("Alterado com sucesso");
            
            } else {
                
               edicao.setMessage("Falha ao tentar alterar!");
            }
            mCon.disconnect();
        } catch (Exception e) {
            edicao.setError(true);
            edicao.setMessage("Falha ao Alterar a Edicao\n\t"+ e.getMessage());
        }
    
    }

    public ArrayList<Edicao> buscarEdicao(Edicao edicao, String tipo) {
        
        try {
            // Connect with database
            MySQLConnector mCon = new MySQLConnector();
            Connection con = mCon.connect();
            
            // List of Buses
            ArrayList<Edicao> newList = new ArrayList<>();
            
            ResultSet rs;
            PreparedStatement stm;
            /*
            DEFAULT - Busca todos as poss�veis
            */
            int cont = 1;
            switch(tipo) {
                case "ANO":
                    stm  = con.prepareStatement("SELECT * FROM edicao "
                            + "WHERE EXTRACT(YEAR from dataInicio) = ?");

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime( edicao.getDataInicio() );
                    
                    stm.setInt(cont++, calendar.get(Calendar.YEAR));
                    
                    //System.out.println(calendar.get(Calendar.YEAR));
                    
                    break;
                default: 
                    stm  = con.prepareStatement("SELECT * FROM edicao");
                    break;
            }
            
            rs = stm.executeQuery();
                    
            while (rs.next()) {
                Edicao edicaoVO = Util.criarEdicao(rs);
                
                if(edicaoVO == null) {
                    
                    SQLException e = new SQLException();
                    throw e;
                }
                newList.add(edicaoVO);
            }
            
            mCon.disconnect();
            return newList;
        }
        catch(Exception e) {
            edicao.setError(true);
            edicao.setMessage("\tFalha T�cnica\n\t" + e.getMessage());
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    public boolean excluirEdicao(Edicao edicao, String tipo) {
        try {
            // Connect with database
            MySQLConnector mCon = new MySQLConnector();
            Connection con = mCon.connect();
                 
            // SQL que vai ser executada
            PreparedStatement stm;
            switch(tipo) {
                case "ALL": 
                    stm = con.prepareStatement(
                            "DELETE FROM edicao WHERE idEdicao > 0");
                    break;
                default: 
                    stm = con.prepareStatement(
                            "DELETE FROM edicao WHERE idEdicao = ?");
                    stm.setInt(1, edicao.getIdEdicao());
                    break;
            }
            
            // Confere se alguma linha do BD foi modificada
            int status = stm.executeUpdate();
            
            if(status == 1) {
                edicao.setError(false);
                edicao.setMessage("Exclu�do com Sucesso!");
                
            }
            else {
                edicao.setError(true);
                edicao.setMessage("Falha ao Excluir!");
            }
            
            mCon.disconnect();
            return !edicao.isError();
        }
        catch(Exception e) {
            edicao.setError(true);
            edicao.setMessage("\tFalha T�cnica\n\t" + e.getMessage());
            return false;
        }
    }

}