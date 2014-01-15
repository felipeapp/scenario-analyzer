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
				<font color="red"><b> ATENÇÃO!</b></font><br/><br/>
	
				Caro Docente, o projeto: "<b> <c:out value="${projetoMonitoria.obj.titulo}" /> </b>" foi Gravado com sucesso. <br/><br/>
				
				Ele está Salvo em nosso banco de dados e poderá ser alterado por você a qualquer momento.<br/><br/>
				
				<h:form id="formGravar">
					<c:if test="${projetoMonitoria.podeEnviarProjeto}">
						Mas, se esta proposta já contém todos os itens obrigatórios e se você <b>NÃO</b> pretende Alterá-la depois, clique no botão abaixo para enviá-la 
						para os chefes de departamentos e seguir com o processo de aprovação.<br/>
		
						Somente após o envio e autorização dos chefes de todos os departamentos envolvidos,
						é que seu projeto será encaminhado à Pró-Reitoria de Graduação (PROGRAD)
						análise da Comissão de Monitoria.<br/><br/>
		
						
						Mas Atenção, ao clicar no botão, você <b>NÃO</b> poderá alterar esta Proposta depois.<br/><br/>
					
						<h:commandButton value="Proposta concluída, finalizar edição agora e Enviar Projeto para Departamentos" action="#{ projetoMonitoria.enviarProjetoPrograd }" id="btFinalizar"/>
						<br/>
						<br/>
					</c:if>					
					<br/>
					<h:commandButton value="NÃO finalizar agora, apenas Gravar e permitir alterar depois" action="#{ projetoMonitoria.cancelar }" id="btSoGravar"/>				
				</h:form>
				
			</center>	
	
	</c:if>
	
	
	<c:if test="${(projetoMonitoria.obj.situacaoProjeto.id ne MON_CADASTRO_EM_ANDAMENTO) and (projetoMonitoria.obj.situacaoProjeto.id ne PROJETO_BASE_CADASTRO_EM_ANDAMENTO)}">
	
			<center><font color="red"><b> ATENÇÃO!</b></font><br/><br/>
			
			Caro Docente, seu projeto foi Enviado com sucesso. <br/><br/>
								
			Para que a pró-reitoria de graduação receba o projeto é necessária
			a autorização, realizada no SIGAA, do chefe do departamento.	<br/><br/>
						
							
			Comunique ao chefe do departamento dos componentes curriculares cadastrados para  
			validarem este projeto. Caso o projeto inclua disciplinas de mais de um departamento
			é necessária a validação de todos os departamentos envolvidos. <br/><br/>

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
				<font color="red"><b> ATENÇÃO!</b></font><br/><br/>	
				Caro Docente, o projeto: "<b> <c:out value="${projetoMonitoria.obj.titulo}" /> </b>" foi Gravado com sucesso. <br/><br/>				
				Ele está Salvo em nosso banco de dados e poderá ser alterado por você a qualquer momento.<br/><br/>				
				<h:form id="formPrint">
					<h:commandButton value="NÃO finalizar agora, apenas Gravar e permitir alterar depois" action="#{ projetoMonitoria.cancelar }" id="btSoGravar"/>				
				</h:form>
			</center>	
		</c:if>
	
		<c:if test="${(projetoMonitoria.obj.situacaoProjeto.id ne MON_CADASTRO_EM_ANDAMENTO) and (projetoMonitoria.obj.situacaoProjeto.id ne PROJETO_BASE_CADASTRO_EM_ANDAMENTO)}">
		
			<center><font color="red"><b> ATENÇÃO!</b></font><br/><br/>
				Caro Docente, seu projeto foi Enviado com sucesso. <br/><br/>
				Para visualizar o resumo do projeto clique no botão abaixo.
			</center>	
			<h:form id="formI">	
				<input type="hidden" name="id" value="${projetoMonitoria.obj.id}"/>	
				<br/><center> <h:commandButton value="Imprimir Resumo do Projeto..." action="#{projetoMonitoria.view}" id="btPrint"/> </center><br/>
			</h:form>
		</c:if>
		
	</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>