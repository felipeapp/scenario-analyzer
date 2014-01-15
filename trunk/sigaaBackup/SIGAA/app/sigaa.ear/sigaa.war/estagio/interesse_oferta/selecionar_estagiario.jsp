<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="interesseOfertaMBean" />
<h2> <ufrn:subSistema /> &gt; Selecionar Estagi�rio</h2>

<div class="descricaoOperacao">
	<p><b>Caro Usu�rio,</b></p>
	<br/>
	<p>Nesta tela voc� dever� informar os dados do Supervisor do Discente no est�gio e as atividades
	 que ser�o exercidas durante o est�gio.</p>
	<br/>
	<p><b>ATEN��O!</b></p>
	<p>O Est�gio s� ser� efetivado ap�s o Cadastramento e sua Aprova��o, que ser� realizado pelo o Coordenador do Curso ao qual o discente
	selecionado pertence.</p>	 
</div>

<c:set var="oferta" value="#{interesseOfertaMBean.obj.oferta}"/>
<%@include file="/estagio/oferta_estagio/include/_oferta.jsp"%>

<c:set var="discente" value="#{interesseOfertaMBean.obj.discente.discente}"/>
<%@include file="/estagio/estagio/include/_discente.jsp"%>
<br/>
<h:form id="form">
	<table class="formulario" width="80%">
		<caption>Dados do Est�gio</caption>
		<a4j:region id="dadosSupervisor">
			<tr>
				<th width="20%" class="obrigatorio">
					CPF do Supervisor: 
				</th>
				<td>						
					<h:inputText id="cpf" onkeypress="return formataCPF(this, event, null)"	value="#{interesseOfertaMBean.supervisor.cpf_cnpj}" size="19" maxlength="14">
						<f:converter converterId="convertCpf"/>
						<f:param name="type" value="cpf" />
						<a4j:support actionListener="#{interesseOfertaMBean.buscarCPF}" event="onchange" reRender="supervisor"/>
					</h:inputText>					
					<ufrn:help>O Supervisor ser� a pessoa que acompanhar� o est�gio do Discente na Institui��o Concedente.</ufrn:help>									
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Nome do Supervisor:</th>
				<td>																																				
					<h:inputText id="supervisor" value="#{interesseOfertaMBean.supervisor.nome}" onkeyup="CAPS(this);" 
					size="60" maxlength="80"/>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">E-mail do Supervisor:</th>
				<td>																																				
					<h:inputText id="supervisorEmail" value="#{interesseOfertaMBean.supervisor.email}" size="60" maxlength="80"/>
				</td>
			</tr>			
		</a4j:region>
		<tr>
			<td class="subFormulario obrigatorio" colspan="2">Descri��o das Atividades<span class="obrigatorio">&nbsp;</span></td>		
		</tr>
		<tr>
			<td colspan="2" align="center">
				<h:inputTextarea value="#{interesseOfertaMBean.obj.descricaoAtividades}" cols="100" rows="8"/>
				<ufrn:help>Descri��o das Atividades a serem exercidas pelo Discente no Est�gio.</ufrn:help>
			</td>
		</tr>			
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Cadastrar" action="#{interesseOfertaMBean.cadastrar}" id="btCadastrar"/>
					<h:commandButton value="<< Voltar" action="#{interesseOfertaMBean.redirectView}" id="btVoltar"/>
					<h:commandButton value="Cancelar" action="#{interesseOfertaMBean.cancelar}" onclick="#{confirm}" immediate="true" id="btCancel"/>
				</td>
			</tr>
		</tfoot>		
	</table>

	<br/><%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>