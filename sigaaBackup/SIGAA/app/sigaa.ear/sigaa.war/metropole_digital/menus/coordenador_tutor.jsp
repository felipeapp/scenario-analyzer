<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>

<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
    <ul style="list-style-image: none; list-style: none;">
    <li> Acompanhamentos
    <ul>
        <li>
	        <h:commandLink
				action="#{relatoriosCoordenadorTutoresIMD.listagemExecucaoFrequenciaNotas}"
				value="Execução da Frequência e Notas Semanais" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
                
    </ul>
    </li>
    
    <li>Tutores
    	<ul> 
   			<ul>
   				<li><a href="${ctx}/metropole_digital/pessoa/busca_geral.jsf?aba=turma">Cadastrar</a></li>
	   			<li><a href="${ctx}/metropole_digital/tutor_imd/lista.jsf?aba=turma">Listar/Alterar</a></li>
	   			<li><a href="${ctx}/metropole_digital/tutor_imd/logar_como.jsf?aba=turma">Logar como tutor</a></li>
   			</ul>
   		</li>
    	</ul>
    </li>
  </ul>