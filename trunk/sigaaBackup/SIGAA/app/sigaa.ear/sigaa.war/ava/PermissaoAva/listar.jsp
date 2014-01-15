
<%@include file="/ava/cabecalho.jsp"%>

<f:view>

<style>
	.botao-medio {
			margin-bottom:0px !important;
			height:60px !important;
	}
</style>

<%@include file="/ava/menu.jsp" %>
<h:form>


<c:set var="permissoes" value="#{permissaoAva.listagem}"/>

<fieldset>
<legend>Permiss�es</legend>

<div class="menu-botoes" style="text-align:center;width:210px;margin:0 auto;">
	<ul class="menu-interno">
			<li class="botao-medio novaPermissao;">
				<h:commandLink action="#{ permissaoAva.preCadastrar }">
					<p style="margin-left:20px;font-variant:small-caps;font-size:1.3em;font-weight:bold;">Cadastrar Permiss�o</p> 
				</h:commandLink>
			</li>
	</ul>	
	<div style="clear:both;"></div>	
</div>

<div class="infoAltRem">
	<img src="/sigaa/ava/img/page_edit.png">: Alterar Permiss�o
	<img src="/sigaa/ava/img/bin.png">: Remover Permiss�o
</div>

<c:if test="${ empty permissoes }">
<p class="empty-listing">Nenhum item foi encontrado</p>
</c:if>
<c:if test="${ not empty permissoes }">
<table class="listing">
	<thead>
	<tr>
		<th><P align="left">CPF</P></th>
		<th><P align="left">Pessoa</P></th>
		<th>PD</th>
		<th>GF</th>
		<th>GE</th>
		<th>GT</th>
		<th>CT</th>
		<th>IA</th>
		<th></th>
		<th></th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="#{ permissoes }" var="item" varStatus="loop">
		<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			<td class="first"><ufrn:format type="cpf_cnpj" valor="${item.pessoa.cpf_cnpj}"></ufrn:format></td>
			<td>${item.pessoa.nome}</td>
			<td align="center">${ item.docente ? 'Sim' : 'N�o' }</td>
			<td align="center">${ item.forum ? 'Sim' : 'N�o' }</td>
			<td align="center">${ item.enquete ? 'Sim' : 'N�o' }</td>
			<td align="center">${ item.tarefa ? 'Sim' : 'N�o' }</td>
			<td align="center">${ item.corrigirTarefa ? 'Sim' : 'N�o' }</td>
			<td align="center">${ item.inserirArquivo ? 'Sim' : 'N�o' }</td>
    		<td class="icon"><h:commandLink action="#{ permissaoAva.editar }" title="Alterar Permiss�o"><f:param name="id" value="#{ item.id }"/><h:graphicImage value="/ava/img/page_edit.png"/></h:commandLink></td>
    		<td class="icon"><h:commandLink action="#{ permissaoAva.remover }" title="Remover Permiss�o" styleClass="confirm-remover" onclick="#{confirmDelete}"><f:param name="id" value="#{ item.id }"/><h:graphicImage value="/ava/img/bin.png"/></h:commandLink></td>
		</tr>
	</c:forEach>
	</tbody>
</table>
<span class="descricao-campo">
PD: Permiss�o Docente;
GF: Gerenciar F�runs;
GE: Gerenciar Enquetes;
GT: Gerenciar Tarefas;
CT: Corrigir Tarefas;
IA: Inserir Arquivos
</span>
</c:if>

</fieldset>

</h:form>

</f:view>
<%@include file="/ava/rodape.jsp"%>
