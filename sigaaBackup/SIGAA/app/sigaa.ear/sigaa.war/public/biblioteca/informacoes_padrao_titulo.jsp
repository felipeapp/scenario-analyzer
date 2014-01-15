
<%@page contentType="text/html; charset=ISO-8859-1" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<style type="text/css">
	
	#tabelaInterna tbody{
		background-color: transparent;
	}
	
</style>

<script type="text/javascript">

	function mostrarResumo() {
		var resumo = $('resumo');
		if ( resumo.visible() ) {
			resumo.hide();
			$('mostrarResumo').show();
			$('ocultarResumo').hide();
		} else {
			resumo.show();
			$('mostrarResumo').hide();
			$('ocultarResumo').show();
		}
	}

</script>

<table class="visualizacao">
	<caption>Dados do Título</caption>
	
	<tr>
		<th width="33%"> Registro no Sistema: </th>
		<td width="67%"> ${_titulo.numeroDoSistema} </td>
	</tr>
	
	<tr style="margin-top: 20px;">
		<th width="33%"> Número de Chamada: </th>
		<td width="67%"> ${_titulo.numeroChamada} </td>
	</tr>
	
	<c:if test="${not empty _titulo.autor}">	
		<tr>
			<th> Autor: </th>
			<td>
				${_titulo.autor}
			</td>
		</tr>
	</c:if>
	
	
	<tr>
		<th width="33%"> Título: </th>
		<td width="67%"> ${_titulo.titulo} ${_titulo.meioPublicacao} </td>
	</tr>
	<c:if test="${not empty _titulo.subTitulo}">
		<tr>
			<th width="20%"> SubTítulo: </th>
			<td> ${_titulo.subTitulo} </td>
		</tr>
	</c:if>
	
	<c:if test="${fn:length(_titulo.formasVariantesTituloFormatadas) > 0 }">
		<tr>
			<th style="#FFFFFF; vertical-align: top; padding-top: 7px;"> Formas Variantes do Título: </th>
			<td>
			<table style="width: 100%;" id="tabelaInterna1">
				<c:forEach items="${_titulo.formasVariantesTituloFormatadas}" var="variante">
				<tr>
					<td>
						${variante}
					</td>
				</tr>
				</c:forEach>
			</table>
		</td>
		</tr>
	</c:if>
	
	<c:if test="${fn:length(_titulo.locaisPublicacaoFormatados) > 0 }">
	<tr>
		<th style="#FFFFFF; vertical-align: top; padding-top: 7px;"> Local da Publicação: </th>
		<td>
			<table style="width: 100%;" id="tabelaInterna2">
				<c:forEach items="${_titulo.locaisPublicacaoFormatados}" var="local">
				<tr>
					<td>
						${local}
					</td>
				</tr>
				</c:forEach>
			</table>
		</td>
	</tr>
	</c:if>
	
	
	
	
	<c:if test="${fn:length(_titulo.editorasFormatadas) > 0 }">	
		<tr>
			<th> Editora: </th>
			<td>
				<table style="width: 100%;" id="tabelaInterna3">
					<c:forEach items="${_titulo.editorasFormatadas}" var="editora">
					<tr>
						<td>
							${editora}
						</td>
					</tr>
					</c:forEach>
				</table>
			</td>
		</tr>
	</c:if>
	
	<c:if test="${fn:length(_titulo.anosFormatados) > 0 }">
		<tr>
			<th style="#FFFFFF; vertical-align: top; padding-top: 7px;"> Ano Publicação: </th>
			<td>
			<table style="width: 100%;" id="tabelaInterna4">
				<c:forEach items="${_titulo.anosFormatados}" var="ano">
				<tr>
					<td>
						${ano}
					</td>
				</tr>
				</c:forEach>
			</table>
		</td>
		</tr>
	</c:if>
	
	
	<c:if test="${fn:length(_titulo.descricaoFisicaFormatada) > 0 }">
		<tr>
			<th style="#FFFFFF; vertical-align: top; padding-top: 7px;"> Descrição Física: </th>
			<td>
			<table style="width: 100%;" id="tabelaInterna5">
				<c:forEach items="${_titulo.descricaoFisica}" var="desc">
				<tr>
					<td>
						${desc}
					</td>
				</tr>
				</c:forEach>
			</table>
		</td>
		</tr>
	</c:if>
	
	<c:if test="${fn:length(_titulo.serieFormatados) > 0 }">
		<tr>
			<th style="#FFFFFF; vertical-align: top; padding-top: 7px;"> Série: </th>
			<td>
			<table style="width: 100%;" id="tabelaInterna6">
				<c:forEach items="${_titulo.serie}" var="ser">
				<tr>
					<td>
						${ser}
					</td>
				</tr>
				</c:forEach>
			</table>
		</td>
		</tr>
	</c:if>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	<c:if test="${fn:length(_titulo.notasGeraisFormatadas) > 0 }">
		<tr>
			<th style="#FFFFFF; vertical-align: top; padding-top: 7px;"> Notas Gerais: </th>
			<td>
			<table style="width: 100%;" id="tabelaInterna7">
				<c:forEach items="${_titulo.notasGeraisFormatadas}" var="notaGeral">
				<tr>
					<td>
						${notaGeral}
					</td>
				</tr>
				</c:forEach>
			</table>
		</td>
		</tr>
	</c:if>
	
	<c:if test="${ not empty _titulo.resumo }">
		<tr>
			<th style="vertical-align: top; padding-top: 7px;">
				Resumo:
			</th>
			<td>
				<a id="mostrarResumo" href="#" onclick="mostrarResumo()">
					Mostrar resumo
					<h:graphicImage title="Clique para ver o resumo" url="/img/blue-folder-closed.png" style="vertical-align: middle;"/>
				</a>
				<a id="ocultarResumo" href="#" onclick="mostrarResumo()" style="display: none;">
					Ocultar resumo 
					<h:graphicImage title="Clique para ocultar o resumo" url="/img/blue-folder-open.gif" style="vertical-align: middle;"/>
				</a>
				<div id="resumo" style="display: none;">
					<br/>
					<c:forEach var="r" items="#{ _titulo.resumosFormatados }" >
						<p> ${r} </p><br/>
					</c:forEach>
				</div>
			</td>
		</tr>
	</c:if>
	
	<c:if test="${fn:length(_titulo.notasConteudoFormatadas) > 0 }">
		<tr>
			<th style="#FFFFFF; vertical-align: top; padding-top: 7px;"> Notas de Conteúdo: </th>
			<td>
			<table style="width: 100%;" id="tabelaInterna8">
				<c:forEach items="${_titulo.notasConteudoFormatadas}" var="notaConteudo">
				<tr>
					<td>
						${notaConteudo}
					</td>
				</tr>
				</c:forEach>
			</table>
		</td>
		</tr>
	</c:if>
	
	
	<c:if test="${fn:length(_titulo.notasLocaisFormatadas) > 0 }">
		<tr>
			<th style="#FFFFFF; vertical-align: top; padding-top: 7px;"> Notas Locais: </th>
			<td>
			<table style="width: 100%;" id="tabelaInterna9">
				<c:forEach items="${_titulo.notasLocaisFormatadas}" var="notaLocais">
				<tr>
					<td>
						${notaLocais}
					</td>
				</tr>
				</c:forEach>
			</table>
		</td>
		</tr>
	</c:if>
	
	<c:if test="${fn:length(_titulo.ISBNFormatados) > 0 }">
		<tr>
			<th style="#FFFFFF; vertical-align: top; padding-top: 7px;"> ISBN: </th>
			<td>
			<table style="width: 100%;" id="tabelaInterna10">
				<c:forEach items="${_titulo.ISBNFormatados}" var="isbn">
				<tr>
					<td>
						${isbn}
					</td>
				</tr>
				</c:forEach>
			</table>
		</td>
		</tr>
	</c:if>
	
					
	<c:if test="${fn:length(_titulo.ISSNFormatados) > 0 }">
		<tr>
			<th style="#FFFFFF; vertical-align: top; padding-top: 7px;"> ISSN: </th>
			<td>
			<table style="width: 100%;" id="tabelaInterna11">
				<c:forEach items="${_titulo.ISSNFormatados}" var="issn">
				<tr>
					<td>
						${issn}
					</td>
				</tr>
				</c:forEach>
			</table>
		</td>
		</tr>
	</c:if>
	
	<c:if test="${fn:length(_titulo.edicoesFormatadas) > 0 }">	
		<tr>
			<th> Edição: </th>
			<td>
				<table style="width: 100%;" id="tabelaInterna12">
					<c:forEach items="${_titulo.edicoesFormatadas}" var="edicao">
					<tr>
						<td>
							${edicao}
						</td>
					</tr>
					</c:forEach>
				</table>
			</td>
		</tr>
	</c:if>
	
	<c:if test="${fn:length(_titulo.assuntosFormatados) > 0 }">
	<tr>
		<th style="#FFFFFF; vertical-align: top; padding-top: 7px;"> Assunto: </th>
		<td>
			<table style="width: 100%;" id="tabelaInterna13">
				<c:forEach items="${_titulo.assuntosFormatados}" var="assunto">
				<tr>
					<td>
						${assunto}
					</td>
				</tr>
				</c:forEach>
			</table>
		</td>
	</tr>
	</c:if>
	
	<c:if test="${fn:length(_titulo.autoresSecundariosFormatados) > 0 }">
	<tr>
		<th style="#FFFFFF; vertical-align: top; padding-top: 7px;"> Autores Secundários: </th>
		<td>
			<table style="width: 100%;" id="tabelaInterna14">
				<c:forEach items="${_titulo.autoresSecundariosFormatados}" var="autorSecundario">
				<tr>
					<td>
						${autorSecundario}
					</td>
				</tr>
				</c:forEach>
			</table>
		</td>
	</tr>
	</c:if>
	
	
	
	
	
	
</table>
<br />