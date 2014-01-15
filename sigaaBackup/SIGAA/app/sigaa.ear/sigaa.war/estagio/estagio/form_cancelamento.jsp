<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="estagioMBean" />
	<a4j:keepAlive beanName="buscaEstagioMBean" />
	<h2> <ufrn:subSistema /> &gt; Cancelar Estágio</h2>
	
	<div class="descricaoOperacao">
		<c:if test="${!buscaEstagioMBean.portalCoordenadorGraduacao}">		
			<p>
				<b> Caro Usuário, </b>
			</p> 	
			<p>
				Para efetivação do cancelamento do Estágio, é necessário a aprovação do Coordenador do Curso ao qual o estágio está vinculado.
			</p>
		</c:if>
		<c:if test="${buscaEstagioMBean.portalCoordenadorGraduacao}">		
			<p>
				<b> Caro Coordenador,</b>
			</p> 	
			<p>É necessário informar sua senha para confirmar o Cancelamento do Estágio.</p>
		</c:if>
	</div>
	
	<c:set var="estagio" value="#{estagioMBean.obj}"/>
	<%@include file="/estagio/estagio/include/_dados_estagio.jsp" %>
	<br/>	
	
	<h:form id="form">
		<table class="formulario" width="70%">
			<caption>Cancelamento de Estágio</caption>	
			<c:if test="${!estagioMBean.obj.solicitadoCancelamento}">
			    <tr>
					<td class="subFormulario" colspan="2">Motivo do Cancelamento <span class="obrigatorio">&nbsp;</span></td>
				</tr>
				<tr>
					<td align="center">
						<h:inputTextarea value="#{estagioMBean.obj.motivoCancelamento}" id="motivo" cols="100" rows="4"/>						
					</td>									
				</tr>
			</c:if>									
			<c:if test="${estagioMBean.obj.solicitadoCancelamento}">
				<tr>
					<th style="width: 30%;"><b>Solicitado Por:</b></th>
					<td>${estagioMBean.obj.registroSolicitacaoCancelamento.usuario.nome}</td>
				</tr>			
			    <tr>
					<th><b>Motivo do Cancelamento:</b></th>
					<td>
						<p>${estagioMBean.obj.motivoCancelamento}</p>
					</td>					
				</tr>																											
			</c:if>												
			<tr>
				<td colspan="2">
					<c:set var="exibirApenasSenha" value="true" scope="request" />
					<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>				
				</td>							
			</tr>				
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btConfirmar" value="Confirmar" action="#{estagioMBean.cadastrar}"/>
						<h:commandButton id="btVoltar" value="<< Voltar" action="#{buscaEstagioMBean.telaBusca}"/>
						<h:commandButton id="btCancelar" value="Cancelar" action="#{estagioMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>							
		</table>		
	</h:form>
	
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	</center>	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	