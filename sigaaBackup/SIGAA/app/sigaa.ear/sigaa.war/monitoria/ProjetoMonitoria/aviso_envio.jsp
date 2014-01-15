<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>
<%@page import="br.ufrn.sigaa.monitoria.dominio.TipoProjetoEnsino"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	<h2><ufrn:subSistema /> > Cadastro de Projeto de Ensino</h2>

	<c:set var="MON_CADASTRO_EM_ANDAMENTO" value="<%= String.valueOf(TipoSituacaoProjeto.MON_CADASTRO_EM_ANDAMENTO) %>" scope="application"/>
	<c:set var="PROJETO_BASE_CADASTRO_EM_ANDAMENTO" value="<%= String.valueOf(TipoSituacaoProjeto.PROJETO_BASE_CADASTRO_EM_ANDAMENTO) %>" scope="application"/>



<c:if test="${projetoMonitoria.obj.projetoMonitoria}">

	<c:if test="${(projetoMonitoria.obj.situacaoProjeto.id eq MON_CADASTRO_EM_ANDAMENTO) or (projetoMonitoria.obj.situacaoProjeto.id eq PROJETO_BASE_CADASTRO_EM_ANDAMENTO)}">
				
			<center>
				<font color="red"><b> ATEN��O!</b></font><br/><br/>
	
				Caro Docente, o projeto: "<b> <c:out value="${projetoMonitoria.obj.titulo}" /> </b>" foi Gravado com sucesso. <br/><br/>
				
				Ele est� Salvo em nosso banco de dados e poder� ser alterado por voc� a qualquer momento.<br/><br/>
				
				<h:form id="formGravar">
					<c:if test="${projetoMonitoria.podeEnviarProjeto}">
						Mas, se esta proposta j� cont�m todos os itens obrigat�rios e se voc� <b>N�O</b> pretende Alter�-la depois, clique no bot�o abaixo para envi�-la 
						para os chefes de departamentos e seguir com o processo de aprova��o.<br/>
		
						Somente ap�s o envio e autoriza��o dos chefes de todos os departamentos envolvidos,
						� que seu projeto ser� encaminhado � Pr�-Reitoria de Gradua��o (PROGRAD)
						an�lise da Comiss�o de Monitoria.<br/><br/>
		
						
						Mas Aten��o, ao clicar no bot�o, voc� <b>N�O</b> poder� alterar esta Proposta depois.<br/><br/>
					
						<h:commandButton value="Proposta conclu�da, finalizar edi��o agora e Enviar Projeto para Departamentos" action="#{ projetoMonitoria.enviarProjetoPrograd }" id="btFinalizar"/>
						<br/>
						<br/>
					</c:if>					
					<br/>
					<h:commandButton value="N�O finalizar agora, apenas Gravar e permitir alterar depois" action="#{ projetoMonitoria.cancelar }" id="btSoGravar"/>				
				</h:form>
				
			</center>	
	
	</c:if>
	
	
	<c:if test="${(projetoMonitoria.obj.situacaoProjeto.id ne MON_CADASTRO_EM_ANDAMENTO) and (projetoMonitoria.obj.situacaoProjeto.id ne PROJETO_BASE_CADASTRO_EM_ANDAMENTO)}">
	
			<center><font color="red"><b> ATEN��O!</b></font><br/><br/>
			
			Caro Docente, seu projeto foi Enviado com sucesso. <br/><br/>
								
			Para que a pr�-reitoria de gradua��o receba o projeto � necess�ria
			a autoriza��o, realizada no SIGAA, do chefe do departamento.	<br/><br/>
						
							
			Comunique ao chefe do departamento dos componentes curriculares cadastrados para  
			validarem este projeto. Caso o projeto inclua disciplinas de mais de um departamento
			� necess�ria a valida��o de todos os departamentos envolvidos. <br/><br/>

			</center>	
	
		<h:form id="formPrint">
			<input type="hidden" name="id" value="${projetoMonitoria.obj.id}"/>	
			<br/><center> <h:commandLink value="Imprimir Resumo de Projeto" action="#{projetoMonitoria.view}" id="btPrint"/> </center><br/>
		</h:form>
		
	</c:if>
	
</c:if>

	<c:if test="${!projetoMonitoria.obj.projetoMonitoria}">
	
		<c:if test="${(projetoMonitoria.obj.situacaoProjeto.id eq MON_CADASTRO_EM_ANDAMENTO) or (projetoMonitoria.obj.situacaoProjeto.id eq PROJETO_BASE_CADASTRO_EM_ANDAMENTO)}">
			<center>
				<font color="red"><b> ATEN��O!</b></font><br/><br/>	
				Caro Docente, o projeto: "<b> <c:out value="${projetoMonitoria.obj.titulo}" /> </b>" foi Gravado com sucesso. <br/><br/>				
				Ele est� Salvo em nosso banco de dados e poder� ser alterado por voc� a qualquer momento.<br/><br/>				
				<h:form id="formPrint">
					<h:commandButton value="N�O finalizar agora, apenas Gravar e permitir alterar depois" action="#{ projetoMonitoria.cancelar }" id="btSoGravar"/>				
				</h:form>
			</center>	
		</c:if>
	
		<c:if test="${(projetoMonitoria.obj.situacaoProjeto.id ne MON_CADASTRO_EM_ANDAMENTO) and (projetoMonitoria.obj.situacaoProjeto.id ne PROJETO_BASE_CADASTRO_EM_ANDAMENTO)}">
		
			<center><font color="red"><b> ATEN��O!</b></font><br/><br/>
				Caro Docente, seu projeto foi Enviado com sucesso. <br/><br/>
				Para visualizar o resumo do projeto clique no bot�o abaixo.
			</center>	
			<h:form id="formI">	
				<input type="hidden" name="id" value="${projetoMonitoria.obj.id}"/>	
				<br/><center> <h:commandButton value="Imprimir Resumo do Projeto..." action="#{projetoMonitoria.view}" id="btPrint"/> </center><br/>
			</h:form>
		</c:if>
		
	</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>