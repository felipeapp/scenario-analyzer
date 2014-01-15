<%@ taglib uri="/tags/mf_core" prefix="f" %>
<f:view>
<%@include file="/portais/turma/cabecalho.jsp"%>

<h3>F�runs da Turma</h3>
<h:outputText value="#{ forumMedio.create }"/>
<table style="margin: 20px auto; border: 1px solid #C4D2EB; width: 99%" cellpadding="3">
	<thead>
	<tr style="background-color: #C4D2EB;">
		<td>F�rum</td>
		<td align="center" width="200">Criador por</td>
		<td align="center" width="100">Data</td>
		<c:if test="${ turmaVirtual.docente }"> <%--  or portalTurma.permissao.forum --%>
		<td width="25"></td>
		<td width="25"></td>
		</c:if>
	</tr>
	</thead>
	<c:forEach items="${forumMedio.allTurma}" var="item">
		<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			<td>
				<c:if test="${ item.topicos }"><a href="topicos.jsf?idForum=${item.id}">${item.titulo}</a></c:if>
				<c:if test="${ !item.topicos }"><a href="mensagens.jsf?idForum=${item.id}">${item.titulo}</a></c:if>
				<br/>${ item.descricao }
			</td>
			<td align="center">${ item.usuario.pessoa.nome }</td>
			<td align="center"><fmt:formatDate value="${item.data}" pattern="dd/MM/yyyy HH:mm"/></td>
			<c:if test="${ turmaVirtual.docente }"> <%-- or portalTurma.permissao.forum --%>
			<td>
				<h:form>
				<input type="hidden" value="${item.id}" name="id" /> 
				<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{forumMedio.atualizar}" />
				</h:form>
			</td>
			<td>
				<h:form>
				<input type="hidden" value="${item.id}" name="id" /> 
				<h:commandButton image="/img/delete.gif" alt="Remover" action="#{forumMedio.remover}" onclick="return(confirm('Deseja realmente excluir este registro?'))"/>
				</h:form>
			</td>
			</c:if>
		</tr>
	</c:forEach>
</table>

<c:if test="${ turmaVirtual.docente  }"> <%--or portalTurma.obj.permiteAlunoCriarForum or portalTurma.permissao.forum  --%>
<h:form>
	<h:messages showDetail="true"/>

	<fieldset>
	<legend>Cadastro de F�rum</legend>
	<ul>
		<li>
			<label for="titulo" class="required">T�tulo <span class="required">&nbsp;</span></label>
			<p class="descricao-campo">(T&iacute;tulo do F&oacute;rum)</p>
			<h:inputText id="titulo" value="#{forumMedio.obj.titulo}" readonly="#{forumMedio.readOnly}" style="width: 95%"/>
		</li>
		<li>
			<label for="descricao" class="required">Descri��o <span class="required">&nbsp;</span></label>
			<p class="descricao-campo">(Descri&ccedil;&atilde;o do F&oacute;rum.)</p>
			<t:inputTextarea id="descricao" rows="2" value="#{forumMedio.obj.descricao}" style="width: 95%"/>
		</li>
		<li>
			<label for="tipo" class="required">Tipo <span class="required">&nbsp;</span></label>
			<p class="descricao-campo">(F�rum de Mensagens � um tipo de f�rum em que todas as mensagens s�o vistas em uma mesma p�gina.)</p>
			<p class="descricao-campo">(F�rum de T�picos � um tipo de f�rum em que as mensagens podem ser agrupadas por assunto, atrav�s da cria��o de t�picos de discuss�o.)</p>
			<t:selectOneRadio id="tipo" value="#{forumMedio.obj.topicos}" styleClass="noborder">
				<f:selectItem itemLabel="F�rum de Mensagens" itemValue="false"/>
				<f:selectItem itemLabel="F�rum de T�picos" itemValue="true"/>
			</t:selectOneRadio>
		</li>
		<li>
			<label for="email">Enviar notifica��o por e-mail?</label>
			<p class="descricao-campo">(Selecione sim se desejar enviar um e-mail para todos os participantes da turma avisando sobre a cria��o do F�rum.)</p>
			<t:selectOneRadio id="email" value="#{forumMedio.notificar}" styleClass="noborder">
				<f:selectItem itemLabel="Sim" itemValue="true"/>
				<f:selectItem itemLabel="N�o" itemValue="false"/>
			</t:selectOneRadio>
		</li>
		<li class="botoes">
			<h:inputHidden value="#{forumMedio.confirmButton}" />
			<h:inputHidden value="#{forumMedio.obj.id}" />
			<h:commandButton value="#{forumMedio.confirmButton}" action="#{forumMedio.cadastrar}" /> 
			<h:commandButton value="Cancelar" action="#{forumMedio.cancelar}" />
		</li>
	</ul>
	</fieldset>
</h:form>
</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
