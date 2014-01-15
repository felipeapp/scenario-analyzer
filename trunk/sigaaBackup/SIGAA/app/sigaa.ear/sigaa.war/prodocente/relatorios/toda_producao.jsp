<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
<style>

<!--
.caption{
	font-weight: bold;
}

.linhaPar { background-color: #f9f9f9; }
.linhaImpar { background-color: #fff; }

h3 {
	text-align: center;
	border-bottom: 1px solid #AAA;
	margin: 5px 0 10px;
}

tr.header td {
background-color: #eee; 
border-bottom: 1px solid #555; 
font-weight: bold; 
border: 1px solid #000;
}
tr.parametro td {
padding: 20px 0 0; 
border-bottom: 1px solid #555; 
}
-->
</style>
 
<h:outputText value="#{todaProducao.create}" />

<h3><center>RELATÓRIO DE TODA A PRODUTIVIDADE</center></h3><br />

<table width="100%">
	<tr >
		<td><b>Docente:</b> ${todaProducao.servidor.pessoa.nome}</td>
	</tr>
	<tr> 
		<td><b>Ano Inicial:</b> ${todaProducao.anoInicial}</td>
	</tr>
	<tr class="curso">
		<td><b>Ano Final:</b> ${todaProducao.anoFinal}</td>
	</tr>
	
</table>

<%-- 
<center>
<div>
	<img src="/sigaa/img/prodocente/validacao/validado.gif"> Validado
	<img src="/sigaa/img/prodocente/validacao/invalidado.gif"> Invalidado
	<img src="/sigaa/img/prodocente/validacao/pendente.gif"> Pendente
</div>
</center>
--%>

<c:set var="coordenacaoGrupoPesquisa" value="#{todaProducao.coordenacaoGrupoPesquisa}"/>
<c:if test="${not empty coordenacaoGrupoPesquisa}">
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
<tr class="header">
<td>Coordenação de Base (${ fn:length(coordenacaoGrupoPesquisa) })</td>
</tr>
	<c:forEach items="#{coordenacaoGrupoPesquisa}" var="item" varStatus="status">
		<tr>
			<td><ufrn:format valor="${item.dataCriacao}" type="mes_ano_numero" />
			${item.nome}, ${item.repercursoesTrabGrupo} </td>
		</tr>
	</c:forEach>
</table>
</c:if>
 
<c:set var="relatorioPesquisa" value="#{todaProducao.listaProducoes}"/>
<c:if test="${not empty listaProducoes}">
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
<tr class="header">
<td>Relatório Finalizado (${ fn:length(listaProducoes) }) </td>
</tr>
	<c:forEach items="#{listaProducoes}" var="item"	varStatus="status">
		<tr>
			<td>${item.codigo.ano}, ${item.titulo}, ${item.centro.sigla}</td>
		</tr>
	</c:forEach>
</table>
</c:if>

<c:set var="coordenacaoProjetoExt" value="#{todaProducao.coordenacaoProjetoExterno}"/>
<c:if test="${not empty coordenacaoProjetoExterno}" >
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
<tr class="header">
<td>Coordenação de Projeto Externo (${ fn:length(coordenacaoProjetoExterno) }) </td>
</tr>
	<c:forEach items="#{coordenacaoProjetoExterno}" var="item"	varStatus="status">
		<tr>
			<td>${item.codigo.ano}, ${item.titulo}, ${item.centro.sigla}</td>
		</tr>
	</c:forEach>
</table>
</c:if>

<c:set var="artigos" value="#{todaProducao.artigos}"/>
<c:if test="${not empty artigos}">
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
		<tr class="header">
			<td>Artigos (${ fn:length(artigos) })</td>
		</tr>
	<c:forEach items="#{artigos}" var="item" varStatus="status">
		<tr>
			<td>${item.anoReferencia}, ${item.autores } , ${item.titulo},
			${item.tituloPeriodico} ISSN: ${item.issn }
			</td>
		</tr>	
	</c:forEach>
</table>
<br>
</c:if>

<c:set var="publicacoes" value="#{todaProducao.publicacoes}" />
<c:if test="${not empty publicacoes}">
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
<tr class="header">
	<td>Publicação em Eventos (${ fn:length(publicacoes) })</td>
</tr>
		<c:forEach items="#{publicacoes}" var="item" varStatus="status">
			<tr>
				<td>${item.anoReferencia}, ${item.autores}, ${item.titulo},
				${item.nomeEvento}, ${item.naturezaDesc} 
				</td>
			</tr>
		</c:forEach>
</table>
</c:if>

<c:set var="apresentacaoEvento" value="#{todaProducao.apresentacaoEvento}" />
<c:if test="${not empty apresentacaoEvento}">
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
<tr class="header">
	<td>Apresentação em Eventos (${ fn:length(apresentacaoEvento) }) </td>
</tr>
		<c:forEach items="#{apresentacaoEvento}" var="item" varStatus="status">
			<tr>
				<td>${item.anoReferencia}, ${item.titulo}, ${item.evento},
				${item.tipoEvento.descricao}
				</td>
			</tr>
		</c:forEach>
</table>
	<br>
</c:if>

<c:set var="capitulos" value="#{todaProducao.capitulos}" />
<c:if test="${not empty capitulos}">
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
<tr class="header">
	<td>Capítulos de Livros (${ fn:length(capitulos) }) </td>
</tr>
		<c:forEach items="#{capitulos}" var="item" varStatus="status">
			<tr>
				<td>${item.anoReferencia}, ${item.titulo}, ${item.tituloLivro},
				${item.autores}
				</td>
			</tr>
	</c:forEach>
</table>
<br>
</c:if>

<c:set var="livros"	value="#{todaProducao.livro}" />
<c:if test="${not empty livros}">
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
<tr class="header">
	<td>Livros (${ fn:length(livros) }) </td>
</tr>
	<c:forEach items="#{livros}" var="item" varStatus="status">
		<tr>
			<td> ${item.anoReferencia}, ${item.titulo}, ${item.autores } </td>
		</tr>
	</c:forEach>
</table>
<br>
</c:if>

<c:set var="textoDidatico"	value="#{todaProducao.textoDidatico}" />
<c:if test="${not empty textoDidatico}">
<tr class="header">
	<td>Textos Didáticos (${ fn:length(textoDidatico) })</td>
</tr>
	<c:forEach items="#{textoDidatico}" var="item" varStatus="status">
		<tr>
			<td> ${item.anoReferencia}, ${item.titulo}, ${item.autores } </td>>
		</tr>
	</c:forEach>
<br>
</c:if>

<c:set var="textoDiscussao"	value="#{todaProducao.textoDiscussao}" />
<c:if test="${not empty textoDiscussao}">
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
<tr class="header">
	<td>Textos para Discussão (${ fn:length(textoDiscussao) })</td>
</tr>
	<c:forEach items="#{textoDiscussao}" var="item" varStatus="status">
		<tr>
			<td> ${item.anoReferencia}, ${item.titulo}, ${item.autores } </td>
		</tr>
	</c:forEach>
</table>
<br>
</c:if>

<c:set var="producoesArtisticas" value="#{todaProducao.producoesArtisticas}" />
<c:if test="${not empty producoesArtisticas}">
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
<tr class="header">
	<td>Produção Artistica, Literária e Visual (${ fn:length(producoesArtisticas) })</td>
</tr>
	<c:forEach items="#{producoesArtisticas}" var="item" varStatus="status">
		<tr>
			<td> ${item.anoReferencia}, ${item.titulo}, ${item.tipoArtistico.descricao}, ${item.subTipoArtistico.descricao}, ${item.autores}</td>
		</tr>
	</c:forEach>
</table>
<br>
</c:if>

<c:set var="producaoTecnologica" value="#{todaProducao.producaoTecnologica}" />
<c:if test="${not empty producaoTecnologica}">
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
<tr class="header">
	<td>Produções Tecnológicas (${ fn:length(producaoTecnologica) })</td>
</tr>
	<c:forEach items="#{todaProducao.producaoTecnologica}" var="item" varStatus="status">
			<tr>
				<td> ${item.anoReferencia}, ${item.titulo}, ${item.autores} </td>
			</tr>
	</c:forEach>
</table>
<br />
</c:if>

<c:set var="bancasConcurso"	value="#{todaProducao.bancasConcurso}" />
<c:if test="${not empty bancasConcurso}">
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
<tr class="header">
<td>Participações em Bancas de Concursos (${ fn:length(bancasConcurso) }) </td>
</tr>
	<c:forEach items="#{bancasConcurso}" var="item" varStatus="status">
		<tr>		
			<td> ${item.anoReferencia},  ${item.titulo}, ${item.autor } </td>
		</tr>
	</c:forEach>
</table>
<br />
</c:if>

<c:set var="bancasCurso" value="#{todaProducao.bancasCurso}" />
<c:if test="${not empty bancasCurso}">
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
<tr class="header">
	<td>Participações em Bancas de Cursos (${ fn:length(bancasCurso) })</td>
</tr>
	<c:forEach items="#{bancasCurso}" var="item" varStatus="status">
		<tr>
			<td> ${item.anoReferencia}, ${item.naturezaExame.descricao }  ${item.titulo}, ${item.autor } </td>
		</tr>
	</c:forEach>
</table>
<br />
</c:if>

<c:set var="premioRecebido"	value="#{todaProducao.premioRecebido}" />
<c:if test="${not empty premioRecebido}">
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
<tr class="header">
<td>Prêmio Recebido (${ fn:length(premioRecebido) }) </td>
</tr>
	<c:forEach items="#{premioRecebido}" var="item" varStatus="status">
		<tr>
			<td> ${item.anoReferencia }, ${item.premio}, ${item.tipoRegiao.descricao} </td>
		</tr>
	</c:forEach>
	</table>
<br />
</c:if>

<c:set var="bolsaObtida" value="#{todaProducao.bolsaObtida}" />
<c:if test="${not empty bolsaObtida}">
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
<tr class="header">
<td> Bolsas Obtidas (${ fn:length(bolsaObtida) }) </td>
</tr>
<c:forEach items="#{bolsaObtida}" var="item" varStatus="status">
		<tr>
			<td> ${item.tipoBolsaProdocente.descricao}, ${item.instituicao },
			<ufrn:format valor="${item.periodoInicio}" type="mes_ano_numero" />,
			<ufrn:format valor="${item.periodoFim}"	type="mes_ano_numero" />,
			</td>
		</tr>
</c:forEach>
</table>
<br />
</c:if>

<c:set var="monitoria" value="#{todaProducao.bolsasMonitoria}" />
<c:if test="${not empty monitoria}">
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
<tr class="header">
<td> Bolsas Monitoria (${ fn:length(monitoria) }) </td>
</tr>
<c:forEach items="#{monitoria}" var="item" varStatus="status">
		<tr>
			<td> ${item.projetoEnsino.ano}, ${item.discente.pessoa.nome}, ${item.projetoEnsino.titulo} </td>
		</tr>
</c:forEach>
</table>
<br />
</c:if>

<c:set var="bolsasIC" value="#{todaProducao.bolsasIC}" />
<c:if test="${not empty bolsasIC}">
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
<tr class="header">
<td> Bolsas de Iniciação Científica (${ fn:length(bolsasIC) }) </td>
</tr>
<c:forEach items="#{bolsasIC}" var="item" varStatus="status">
		<tr>
			<td> ${item.planoTrabalho.projetoPesquisa.codigo.ano}, ${item.planoTrabalho.projetoPesquisa.titulo},
			${item.discente.pessoa.nome} </td>
		</tr>
</c:forEach>
</table>
<br />
</c:if>

<c:set var="visitaCientifica" value="#{todaProducao.visitaCientifica}" />
<c:if test="${not empty visitaCientifica}">
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
<tr class="header">
<td>Visitas Científicas (${ fn:length(visitaCientifica) })</td>
</tr>
	<c:forEach items="#{visitaCientifica}" var="item" varStatus="status">
		<tr>
			<td> ${item.anoReferencia }, ${item.titulo}, ${item.local } </td>
		</tr>
	</c:forEach>
</table>
<br />
</c:if>

<c:set var="organizacaoEventos" value="#{todaProducao.organizacaoEventos}"/>
<c:if test="${not empty organizacaoEventos}">
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
<tr class="header">
<td>Organização de Eventos, Consultorias, Edição e Revisão de
Períodicos (${ fn:length(organizacaoEventos) })</td>
</tr>
	<c:forEach items="#{organizacaoEventos}" var="item" varStatus="status">
		<tr>
			<td> ${item.anoReferencia },${item.tipoParticipacaoOrganizacao.descricao}, ${item.local }, 
					${item.ambito.descricao}, ${item.veiculo} </td>
		</tr>
	</c:forEach>
</table>
<br />
</c:if>

<c:set var="participacaoSociedade" value="#{todaProducao.participacaoSociedade}"/>
<c:if test="${not empty participacaoSociedade}">
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
<tr class="header">
<td>Participação em Sociedades Científicas e Culturais (${ fn:length(participacaoSociedade) })</td>
</tr>
	<c:forEach items="#{todaProducao.participacaoSociedade}" var="item" varStatus="status">
		<tr>
			<td> ${item.anoReferencia }, ${item.nomeSociedade}, ${item.tipoParticipacaoSociedade.descricao } </td>
		</tr>
	</c:forEach>
</table>
<br />
</c:if>

<c:set var="participacaoColegiado" value="#{todaProducao.participacaoColegiado}"/>
<c:if test="${not empty participacaoColegiado}">
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
<tr class="header">
<td>Participação em Colegiados e Comissões (${ fn:length(participacaoColegiado) })</td>
</tr>
	<c:forEach items="#{participacaoColegiado}" var="item" varStatus="status">
		<tr>
			<td> ${item.comissao},${item.tipoComissaoColegiado.descricao }, ${item.tipoMembroColegiado.descricao },  
				${item.instituicao.nome} </td>
		</tr>
	</c:forEach>
</table>
<br />
</c:if>

<c:set var="fimCurso" value="#{todaProducao.trabalhoFimCurso}"/>
<c:if test="${not empty fimCurso}">
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
<tr class="header">
<td>Trabalho de Fim de Curso(${ fn:length(fimCurso) })</td>
</tr>
	<c:forEach items="#{fimCurso}" var="item" varStatus="status">
		<tr>
			<td>${item.titulo}, ${item.orientando.pessoa.nome} ${item.orientandoString},
			<ufrn:format valor="${item.dataDefesa}" type="mes_ano_numero"/>
			</td>
		</tr>
	</c:forEach>
</table>
<br />
</c:if>

<c:set var="orientacoes" value="#{todaProducao.orientacoes}"/>
<c:if test="${not empty orientacoes}">
<table cellspacing="1" width="100%" style="font-size: 10px;" border="1">
<tr class="header">
<td>Orientações Pós-Graduação Concluidas em ${todaProducao.anoFinal} 
		ou em andamento (${ fn:length(orientacoes) })</td>
</tr>
	<c:forEach items="#{orientacoes}" var="item" varStatus="status">
		<tr>
			<td> ${item.tipoOrientacao.descricao} 
					<c:if test="${not empty item.titulo}">
						, ${item.titulo} 
					</c:if>
			, ${item.orientando} ${item.orientandoDiscente.pessoa.nome }, 
			<fmt:formatDate value="${item.periodoInicio}" pattern="MM/yyyy/dd"/> - 
			<fmt:formatDate value="${item.periodoFim}" pattern="dd/MM/yyyy"/>
					<c:if test="${item.dataPublicacao != null}">
						, Concluída em <ufrn:format valor="${item.dataPublicacao}" type="mes_ano_numero"/>
					</c:if>
		 	</td>
		 </tr>
	</c:forEach>
  </table>
  <br />
	<em>
	<b>Atenção:</b> As orientações são lançadas pelo Programa de Pós-Graduação. Caso falte alguma ou algum dado esteja incorreto, recorra a este setor.
	É importante atentar acerca da conclusão da orientação, pois o edital de pesquisa considera apenas Orientações Concluídas.
	</em>
<br />
</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>