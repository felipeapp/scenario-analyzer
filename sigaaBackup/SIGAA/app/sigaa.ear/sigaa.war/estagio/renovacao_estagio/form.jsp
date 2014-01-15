<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> &gt; Renovação do Estágio</h2>

<f:view>	
	<a4j:keepAlive beanName="renovacaoEstagioMBean"/>
	<a4j:keepAlive beanName="buscaEstagioMBean"/>
	<h:form id="form">
	<div class="descricaoOperacao">
		<p><b>Prezado Coordenador,</b></p><br />
		<p>Nesta tela você poderá informar a nova data de fim do estágio, assim prorrogando-o até a data informada.</p><br />
		<p><b>ATENÇÃO!</b></p><br />
		<p>Para que a Renovação seja confirmada, é necessário o Preenchimento dos Relatórios de Estágio, que devem serem
		preenchidos pelo Discente, no Portal do Discente, pelo Supervisor do Estágio, no Portal do Concedente de Estágio e pelo 
		Professor  Orientador do Estágio, no Portal do Docente.</p>
	</div>
	
	<c:set var="estagio" value="#{renovacaoEstagioMBean.obj.estagio}"/>
	<%@include file="/estagio/estagio/include/_dados_estagio.jsp" %>
	<br/>
	
	<table class="formulario" width="100%">
		<caption>Dados da Renovação do Estágio</caption>
		
		<tr>
			<th class="obrigatorio">Data de Prorrogação:</th>
			<td>
				<t:inputCalendar popupTodayString="Hoje é" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" renderPopupButtonAsImage="true" size="10"
							maxlength="10" onkeypress="return formataData(this,event)" value="#{renovacaoEstagioMBean.obj.dataRenovacao}" id="dataRenovacao" /> 							
			</td>		
		</tr>	
		
		<tr>
			<th>Observação:</th>
			<td>
				<h:inputTextarea value="#{renovacaoEstagioMBean.obj.observacao}" id="obs" cols="80" rows="3"/>						
			</td>					
		</tr>		
		
		<tr>
			<td colspan="2">
				<c:set var="exibirApenasSenha" value="true" scope="request" />
				<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>			
			</td>
		</tr>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Confirmar" id="confirmar" action="#{renovacaoEstagioMBean.cadastrar}" /> 
					<h:commandButton value="Cancelar" action="#{renovacaoEstagioMBean.cancelar}" id="cancelar" onclick="#{confirm}" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>