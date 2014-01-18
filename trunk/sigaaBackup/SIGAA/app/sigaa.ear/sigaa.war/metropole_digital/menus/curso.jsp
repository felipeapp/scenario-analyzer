<%@ taglib uri="/tags/ufrn" prefix="ufrn"  %>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

    <ul>
        <li>Curso
            <ul>
                <li><h:commandLink action="#{cursoTecnicoMBean.preCadastrar}" value="Cadastrar" onclick="setAba('curso')"/> </li>
 	            <li><h:commandLink action="#{documentoLegalMBean.listar}" value="Consultar Documentos Legais" onclick="setAba('curso')"/> </li>                
            </ul>
        </li>
         
        <li>Cronograma de Execu��o
            <ul>
               	<li><h:commandLink action="#{cronogramaExecucao.preCadastrar}" value="Cadastrar" onclick="setAba('curso')"/></li>
               	<li><h:commandLink action="#{cronogramaExecucao.preBuscar}" value="Listar/Alterar" onclick="setAba('curso')"/></li>
			</ul>
        </li>
       
        <li> Componentes Curriculares
		    <ul>
	    	<li> <h:commandLink value="Cadastrar" action="#{componenteCurricular.preCadastrar}" onclick="setAba('curso')"/> </li>
			<li> <h:commandLink value="Buscar/Alterar" action="#{componenteCurricular.listar}" onclick="setAba('curso')"/> </li>
		    </ul>
	    </li>
        <li>Estrutura Curricular
            <ul>
	            <li><h:commandLink action="#{estruturaCurricularTecnicoMBean.preCadastrar}" value="Cadastrar" onclick="setAba('curso')"/></li>
	            <li><h:commandLink action="#{estruturaCurricularTecnicoMBean.listar}" value="Listar/Alterar" onclick="setAba('curso')"/></li>
			</ul>
 		
 		<li>M�dulos do Curr�culo
            <ul>
               	<li><h:commandLink action="#{moduloMBean.preCadastrar}" value="Cadastrar" onclick="setAba('curso')"/></li>
               	<li><h:commandLink action="#{moduloMBean.listar}" value="Listar/Alterar" onclick="setAba('curso')"/></li>
			</ul>
        </li>
        
        <li> Coordenador de P�lo
   			<ul>
   				<li><a href="${ctx}/metropole_digital/coordenador_polo/busca_pessoa.jsf?aba=curso">Cadastrar</a></li>
   				<li><a href="${ctx}/metropole_digital/coordenador_polo/lista.jsf?aba=curso">Listar/Alterar</a></li>
   			</ul>
   		</li>
   		
   		<li> Coordenador de Tutores 
   			<ul>
   				<li><a href="${ctx}/metropole_digital/coordenador_tutor/busca_pessoa.jsf?aba=curso">Cadastrar</a></li>
   				<li><a href="${ctx}/metropole_digital/coordenador_tutor/lista.jsf?aba=curso">Listar/Excluir</a></li>
   			</ul>
   		</li>
        
   		<li> Opera��es Administrativas 
   			<ul>
   				<li><h:commandLink action="#{parametrosAcademicosIMD.initParametros}" value="Par�metros" onclick="setAba('curso')"/></li>		
   			</ul>
   		</li>        
    </ul>