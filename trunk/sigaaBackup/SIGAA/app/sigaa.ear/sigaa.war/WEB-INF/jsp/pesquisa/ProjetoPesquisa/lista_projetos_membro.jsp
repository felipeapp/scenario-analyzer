<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table.listagem tr td.vazio {
		padding: 10px;
		font-style: italic;
		text-align: center;
	}
</style>


<h2>
	<ufrn:steps /> &gt; Meus Projetos de Pesquisa
</h2>

<center>
	<div class="infoAltRem">
	    <html:img page="/img/view.gif" style="overflow: visible;"/>
	    : Visualizar Projeto de Pesquisa	    
	    <html:img page="/img/alterar.gif" style="overflow: visible;"/>
	    : Alterar Projeto de Pesquisa
	    <html:img page="/img/delete.gif" style="overflow: visible;"/>
	    : Remover Projeto de Pesquisa<br/>
	    <html:img page="/img/pesquisa/finalizar_projeto.png" style="overflow: visible;"/>
	    : Finalizar Projeto de Pesquisa
   	    <html:img page="/img/printer_ok.png" width="20px;" height="20px;" style="overflow: visible;"/>
	    : Imprimir Projeto de Pesquisa
	    <html:img page="/img/icones/menu_sigaa/amb_virt_2.png" width="25px;" height="25px;" style="overflow: visible;"/>
	    : Criar Comunidade Virtual com participantes do projeto<br/>
	    <html:img page="/img/check.png" width="18px;" height="18px;" style="overflow: visible;"/>
	    : Executar Projeto
	    <html:img page="/img/refresh.png" width="18px;" height="18px;" style="overflow: visible;"/>
	    : Vincular Novo Edital
	</div>
</center>

<table class="listagem">
	<caption> Projetos de Pesquisa de que Participo </caption>
	<thead>
		<tr>
			<th> Código </th>
			<th> Título/Coordenador </th>
			<th width="5%"> Tipo </th>
			<th> Situação </th>
			<th> </th>
			<th> </th>
			<th> </th>
			<th> </th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td class="subFormulario" colspan="8"> Projetos passíveis de alteração  </td>
		</tr>
		<c:choose>
			<c:when test="${ not empty listaGravados }">
				<c:forEach var="projeto" items="${listaGravados}" varStatus="status">
					<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td nowrap="nowrap"> ${ projeto.codigo } </td>
						<td> ${ projeto.titulo } </td>
						<td> ${ projeto.interno ? "INT" : "EXT" } </td>
						<td> ${ projeto.situacaoProjeto.descricao } </td>
						<td nowrap="nowrap">
							<ufrn:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa" param="id=${ projeto.id }&dispatch=view">
								<img src="${ctx}/img/view.gif"
									alt="Visualizar Projeto de Pesquisa"
									title="Visualizar Projeto de Pesquisa"/>
							</ufrn:link>
						</td>
						<td nowrap="nowrap">
							<ufrn:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa" param="id=${ projeto.id }&dispatch=edit">
								<img src="${ctx}/img/alterar.gif"
									alt="Alterar Projeto de Pesquisa"
									title="Alterar Projeto de Pesquisa"/>
							</ufrn:link>
						</td>
						<td nowrap="nowrap">
							<ufrn:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa" param="id=${ projeto.id }&dispatch=remove">
								<img src="${ctx}/img/delete.gif"
									alt="Excluir Projeto de Pesquisa"
									title="Excluir Projeto de Pesquisa"/>
							</ufrn:link>
						</td>
						<td colspan="3"></td>
					</tr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<tr>
					<td class="vazio" colspan="8"> Não há projetos abertos para alterações </td>
				</tr>
			</c:otherwise>
		</c:choose>
		<tr>
			<td class="subFormulario" colspan="8"> Projetos Enviados  </td>
		</tr>
		<c:forEach var="projeto" items="${projetos}" varStatus="status">
		<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td nowrap="nowrap"> ${ projeto.codigo } </td>
			<td>
				${ projeto.titulo } <br />
				 <i>${ projeto.coordenador.pessoa.nome }</i>
			</td>
			<td> ${ projeto.interno ? "INT" : "EXT" } </td>
			<td> ${ projeto.situacaoProjeto.descricao } </td>
			<td>
				<ufrn:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa" param="id=${ projeto.id }&dispatch=view">
					<img src="${ctx}/img/view.gif"
						alt="Visualizar Projeto de Pesquisa"
						title="Visualizar Projeto de Pesquisa" />
				</ufrn:link>
			</td>
			<td>
				<ufrn:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa" param="id=${ projeto.id }&dispatch=imprimirProjetoPesquisa">
					<img src="${ctx}/img/printer_ok.png" width="20px;" height="20px;"
						alt="Imprimir Projeto de Pesquisa"
						title="Imprimir Projeto de Pesquisa" />
				</ufrn:link>
			</td>

			<td>
				<c:if test="${projeto.emExecucao}">
					<ufrn:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa" param="id=${ projeto.id }&dispatch=criarComunidadeVirtualPesquisa">
						<img src="${ctx}/img/icones/amb_virt.png" 
							alt="Criar Comunidade Virtual com participantes do projeto"
							title="Criar Comunidade Virtual com participantes do projeto"
							onclick="if (!confirm('Tem certeza que deseja criar uma Comunidade Virtual com os participantes desse projeto?')) return false;">
					</ufrn:link>
				</c:if>
				<c:if test="${projeto.aprovado}">
					<html:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa?id=${ projeto.id }&dispatch=executarProjeto" title="Iniciar Projeto">
							<img src="${ctx}/img/check.png" width="18px;" height="18px;"/>
						</html:link>
				</c:if>
				<c:if test="${projeto.permiteAlterarEdital}">
						<ufrn:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa" param="id=${ projeto.id }&dispatch=edit">
							<img src="${ctx}/img/refresh.png" width="18px;" height="18px;" alt="Vincular Novo Edital" title="Vincular Novo Edital"/>
						</ufrn:link>
				</c:if>
			</td>
			
			<td nowrap="nowrap">
			<c:if test="${projeto.coordenador.pessoa.id == usuario.pessoa.id && projeto.passivelFinalizacaoCoordenador}">
				<ufrn:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa" param="id=${ projeto.id }&dispatch=edit">
					<img src="${ctx}/img/alterar.gif"
						alt="Alterar Projeto de Pesquisa"
						title="Alterar Projeto de Pesquisa"/>
				</ufrn:link>
				<html:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa?id=${ projeto.id }&dispatch=preFinalizar" title="Finalizar Projeto de Pesquisa">
					<img src="${ctx}/img/pesquisa/finalizar_projeto.png" />
				</html:link>
			</c:if>
			</td>
		</tr>
		</c:forEach>
	</tbody>
</table>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
