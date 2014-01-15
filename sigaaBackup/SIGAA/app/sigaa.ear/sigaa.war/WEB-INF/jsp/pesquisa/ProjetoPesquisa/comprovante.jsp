<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>

#comprovanteEnvio {
	background: #F5F5F5;
}

#comprovanteEnvio h4{
	text-align: center;
	font-variant: small-caps;
	background: #404E82;
	padding: 2px;
	color: #FEFEFE;
}

#comprovanteEnvio p{
	padding: 5px;
	text-align: center;
	margin: 20px 100px;
}

#comprovanteEnvio .link{
	text-align: center;
	padding: 10px;
}

</style>

<h2> Projetos de Pesquisa </h2>

<c:set var="projeto" value="${ projetoPesquisaForm.obj }" />

<div id="comprovanteEnvio">

	<c:choose>
		<c:when test="${ projetoPesquisaForm.renovacao }">
			<h4> Comprovante de Renovação de Projeto de Pesquisa </h4>
			<p>
				Projeto <b>${ projeto.codigo }</b>, cadastrado com sucesso
				em <ufrn:format type="datahora" name="projeto" property="dataAtualizacao" />
			 	por ${ projeto.registroAtualizacao.usuario.pessoa.nome }
			 	(usuário <i>${ projeto.registroAtualizacao.usuario.login }</i>)
			</p>
		</c:when>
		<c:otherwise>
			<h4> Comprovante de Cadastro de Projeto de Pesquisa </h4>
			<p>
				Projeto <b>${ projeto.codigo }</b>, cadastrado e submetido com sucesso à Pró-Reitoria de Pesquisa
				em <ufrn:format type="datahora" name="projeto" property="dataCadastro" />
			 	por ${ projeto.registroEntrada.usuario.pessoa.nome }
			 	(usuário <i>${ projeto.registroEntrada.usuario.login }</i>)
			</p>
		</c:otherwise>
	</c:choose>

	
	<table>
		<tr>
			<td align="center">
				<html:link action="/pesquisa/projetoPesquisa/buscarProjetos?dispatch=view&id=${projeto.id}">
					<img src="${ctx}/img/graduacao/submissao_solicitacoes.png"/>
				</html:link>
			</td>
			<td align="center">
				<html:link action="/pesquisa/planoTrabalho/wizard.do?dispatch=iniciarSolicitacaoCota&solicitacaoCota=true&cadastroVoluntario=false">
					<img src="${ctx}/img/graduacao/destrancar_programa.png"/>
				</html:link>
			</td>
		</tr>
		<tr>
			<td width="50%">
			<div class="link">
				<html:link action="/pesquisa/projetoPesquisa/buscarProjetos?dispatch=view&id=${projeto.id}">
					Clique aqui para visualizar o projeto submetido.
				</html:link>
			</div>
			</td>
			<td width="50%">
			<div class="link">
				<html:link action="/pesquisa/planoTrabalho/wizard.do?dispatch=iniciarSolicitacaoCota&solicitacaoCota=true&cadastroVoluntario=false">
					Clique aqui para cadastrar os planos de trabalho para solicitar cotas de bolsas.
				</html:link>
				
			</div>
			</td>
		</tr>
	</table>
</div>

<br />
<div align="center">

</div>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
