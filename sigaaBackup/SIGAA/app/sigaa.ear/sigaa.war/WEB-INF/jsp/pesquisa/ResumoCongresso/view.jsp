<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.pesquisa.dominio.AutorResumoCongresso"%>
<style>
	table.formulario td.campo {
		padding: 4px 12px;
	}

	#link-coAutores {
		line-height: 1.5em;
		background: url(${ctx}/img/pesquisa/user_add.gif) no-repeat 0 50%;
		padding-left: 20px;
		display: block;
		width: 150px;
	}

	#form-autores {
		margin: 0px 2px 5px;
	}

	#coAutores {
		margin: 0 0 5px 0;
		padding: 0;
		list-style: none;
	}

	#coAutores li{
		vertical-align: middle;
	}

	#coAutores span.nome{
		padding-right: 10px;
	}

	div.aba {
		border-bottom: 0;
	}
</style>

<h2><ufrn:subSistema /> &gt; <c:out value="Resumos para Congresso de Iniciação Científica" /></h2>

<c:set var="resumo" value="${objeto}" />

<div class="descricaoOperacao">
	<h3 style="text-align: center; margin-bottom: 15px;"> ${ resumo.congresso.edicao } Congresso de Iniciação Científica </h3>
	<p>
		<b>Período do Congresso:</b>
		<ufrn:format type="data" name="resumo" property="congresso.inicio" /> a
		<ufrn:format type="data" name="resumo" property="congresso.fim" />
	</p>
</div>

<%-- CORPO DO RESUMO --%>
<table class="formulario" width="95%">
<caption> Corpo do Resumo </caption>
<tbody>
	<c:if test="${not empty formResumoCongresso.obj.dataEnvio }">
	<tr>
		<th nowrap="nowrap"> <b>Última alteração em:</b> </th>
		<td>
			<ufrn:format type="dataHora" name="formResumoCongresso" property="obj.dataEnvio"/>
		</td>
	</tr>
	</c:if>
	<tr>
		<td width="15%">
			<b>Autor:</b>
		</td>
		<td>
			${ resumo.autor.nome }
			<c:if test="${acesso.pesquisa}">
			  <em> (CPF: <ufrn:format type="cpf_cnpj" name="resumo" property="autor.cpf" />) </em>
			</c:if>
		</td>
	</tr>
	<tr>
		<td>
			<b>Orientador:</b>
		</td>
		<td> ${ resumo.orientador.nome } </td>
	</tr>

	<c:if test="${ not empty resumo.coAutores}">
		<tr>
			<td valign="top">
				<b>Co-autor(es):</b> <br/>
			</td>
			<td>
				<ul id="coAutores">
					<c:forEach var="autor" items="${ resumo.autores }" varStatus="loop">
						<c:if test="${ autor.coAutor  }">
						<li>
							<span class="nome">${ autor.nome }</span>
						</li>
						</c:if>
					</c:forEach>
				</ul>
			</td>
		</tr>
	</c:if>

	<tr>
		<td colspan="2">
			<b>Área de Conhecimento: </b>
		</td>
	</tr>
	<tr>
		<td colspan="2" class="campo">
			${ resumo.areaConhecimentoCnpq.nome }
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<b>Título</b>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="campo">
				<ufrn:format name="resumo" property="titulo" type="texto"/>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<b>Resumo</b>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="campo">
				<ufrn:format name="resumo" property="resumo" type="texto"/>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<b>Palavras-Chave</b>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="campo">
				<ufrn:format name="resumo" property="palavrasChave" type="texto"/>
			</td>
		</tr>
		<c:if test="${acesso.pesquisa and not empty avaliacao}">
			<tr>
				<td colspan="2" class="subFormulario" style="text-align: center">
					Parecer do Avaliador
				</td>
			</tr>
 			<tr>
				<td colspan="2">
					<b>Possui erros gramaticais?</b> 
				</td>
			</tr>
			<tr>
				<td colspan="2" class="campo">
					<ufrn:format type="simnao" name="avaliacao" property="erroPortugues" />
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<b>Possui erros de conteúdo?</b> 
				</td>
			</tr>
			<tr>
				<td colspan="2" class="campo">
					<ufrn:format type="simnao" name="avaliacao" property="erroConteudo" />
				</td>
			</tr>
			<tr>
			<td colspan="2">
					<b>Parecer</b>
				</td>
			</tr>
			<tr>
				<td colspan="2" class="campo">
					<ufrn:format name="avaliacao" property="parecer" type="texto"/>
				</td>
			</tr>
		</c:if>
	</tbody>
</table>

<c:if test="${empty alteracao}">
<div class="voltar">
	<a href="javascript:history.back();"> Voltar </a>
</div>
</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>