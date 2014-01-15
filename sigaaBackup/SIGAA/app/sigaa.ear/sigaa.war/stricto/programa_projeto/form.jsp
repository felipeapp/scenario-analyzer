<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	table.listagem tr.projeto td{
		background: #C8D5EC;
		font-weight: bold;
	}
</style>

<f:view>
<h:messages showDetail="true"></h:messages>
<h2 class="title"><ufrn:subSistema /> > Vincular projetos de pesquisa ao programa</h2>

<h:outputText value="#{programaProjetoBean.create}" />

<div class="descricaoOperacao">
	<p>Caro Usu�rio,</p>
	<p>
	Nesta opera��o voc� deve selecionar os projetos de pesquisa que s�o desenvolvidos pelo seu programa. 
	Os projetos selecionados ir�o aparecer automaticamente na p�gina p�blica do programa.	
	</p>
</div>

<h:form id="formConsultaProgramaProjeto">

<table class="formulario" width="80%">
	<caption>Consulta</caption>
	
	<tr>
		<td width="2%"><h:selectBooleanCheckbox value="#{programaProjetoBean.checkAno}" id="checkAno"/></td>
		<td>Ano:</td>
		<td>
			<h:inputText value="#{programaProjetoBean.ano}" id="txtAno" size="5" maxlength="4" 
			onkeyup="return formatarInteiro(this);" onfocus="getEl('formConsultaProgramaProjeto:checkAno').dom.checked = true;"/>
		</td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{programaProjetoBean.checkCodigo}" id="checkCodigo"/></td>
		<td>C�digo:</td>
		<td>
			<h:inputText value="#{programaProjetoBean.codigo}" id="txtCodigo" size="12" maxlength="14"
		 	onfocus="getEl('formConsultaProgramaProjeto:checkCodigo').dom.checked = true;" onkeyup="CAPS(this);"/>
		 </td>
	</tr>
	<tr>	
		<td><h:selectBooleanCheckbox value="#{programaProjetoBean.checkTitulo}" id="checkTitulo"/></td>
		<td>T�tulo:</td>
		<td>
			<h:inputText value="#{programaProjetoBean.titulo}" id="txtTitulo" size="70" maxlength="90"
			onfocus="getEl('formConsultaProgramaProjeto:checkTitulo').dom.checked = true;"/>
		</td>
	</tr>
	<tr>
		<td><h:selectBooleanCheckbox value="#{programaProjetoBean.checkMembro}" id="checkMembro"/></td>
		<td>Membro:</td>
		<td>
			<h:inputText value="#{programaProjetoBean.membro}" id="txtMembro" size="70" maxlength="90"
			onfocus="getEl('formConsultaProgramaProjeto:checkMembro').dom.checked = true;" onkeyup="CAPS(this);"/>
		</td>
	</tr>
	<tr>	
		<td><h:selectBooleanCheckbox value="#{programaProjetoBean.checkSituacao}" id="checkSituacao"/></td>
		<td>Situa��o:</td>
		<td>	
			<h:selectOneMenu value="#{programaProjetoBean.situacao}" onchange="getEl('formConsultaProgramaProjeto:checkSituacao').dom.checked = true;" id="oneMenuSituacao">
				<f:selectItems value="#{tipoSituacaoProjeto.situacoesProjetoPesquisaValidos}"/>
			</h:selectOneMenu>
		</td>
	</tr>
	
	<tfoot>
		<tr>
			<td colspan="3">
				<h:commandButton value="Buscar" action="#{programaProjetoBean.buscar}" id="btDeBusca"/>
			</td>
		</tr>
	</tfoot>

</table>
</h:form>

<br/>

<h:form id="formCadastroProgramaProjeto">
<table class="listagem" width="100%">
	<caption>Marque os projetos que s�o desenvolvidos pelo seu programa (${fn:length(programaProjetoBean.projetos)})</caption>
	
	<thead>
		<tr>
			<th width="2%"></th>
			<th width="13%">C�digo</th>
			<th width="75%">Projeto</th>
			<th width="10%">Situa��o</th>
		</tr>
	</thead>
	
	<c:forEach items="#{programaProjetoBean.projetos}" var="projeto">
	
		<tr class="projeto">
			<td><h:selectBooleanCheckbox value="#{projeto.selecionado}" id="checkProjeto"/></td>
			<td>${projeto.codigo}</td>
			<td>${projeto.titulo}</td>
			<td>${projeto.situacaoProjeto}</td>
		</tr>
		
		<c:if test="${not empty projeto.membrosProjeto}">
			<c:forEach items="${projeto.membrosProjeto}" var="membro" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td></td>
					<td></td>
					<td>${membro.servidor.nome}</td>
					<td></td>
				</tr>	
			</c:forEach>
		</c:if>
		
	</c:forEach>
	
	<tfoot>
		<tr>
			<td colspan="4">
				<center>
				<h:commandButton value="Gravar" action="#{programaProjetoBean.cadastrar}" id="btaoGravar"/> 
				<h:commandButton value="Cancelar" action="#{programaProjetoBean.cancelar}"	immediate="true" onclick="#{confirm}" id="adeusOperacao"/>
				</center>
			</td>
		</tr>
	</tfoot>
</table>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>