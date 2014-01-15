<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h:form>

<div class="descricaoOperacao">
<p>Clique no botão "Recuperar" para que seja gerada uma nova senha para o discente selecionado. Caso
deseje selecionar um novo discente, clique em "Selecionar outro discente".</p>
</div>

<table class="formulario">
<caption>Dados do Usuário</caption>
<tr><td>Discente: </td><td>${ recuperarSenhaDiscenteMBean.obj.matriculaNome }</td></tr>
<tr><td>Curso: </td><td>${ recuperarSenhaDiscenteMBean.obj.curso.descricao }</td></tr>
<tr><td>Usuário: </td><td>${ recuperarSenhaDiscenteMBean.usuario.login }</td></tr>
</table>
<br/>
<p align="center">

<c:if test="${ recuperarSenhaDiscenteMBean.novaSenha == null }">
<strong>Deseja recuperar a senha desse discente?</strong><br/><br/>

<h:commandButton value="Recuperar" action="#{ recuperarSenhaDiscenteMBean.recuperar }"/> 
<h:commandButton value="<< Selecionar outro discente" action="#{ recuperarSenhaDiscenteMBean.iniciar }"/> 
<h:commandButton value="Cancelar" action="#{ recuperarSenhaDiscenteMBean.cancelar }"/>
</c:if>

<c:if test="${ recuperarSenhaDiscenteMBean.novaSenha != null }">
A nova senha do discente é: <strong>${ recuperarSenhaDiscenteMBean.novaSenha }</strong> <br/><br/>

<h:commandButton value="Selecionar outro discente" action="#{ recuperarSenhaDiscenteMBean.iniciar }"/> 
<h:commandButton value="Cancelar" action="#{ recuperarSenhaDiscenteMBean.cancelar }"/>
</c:if>

</p>

</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>