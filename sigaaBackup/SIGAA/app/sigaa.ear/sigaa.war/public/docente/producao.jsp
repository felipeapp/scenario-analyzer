<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>

<f:view>

<%@include file="/public/docente/cabecalho.jsp" %>

<div id="id-docente">
	<h3>${fn:toLowerCase(docente.nome)}</h3>
	<p class="departamento">${docente.unidade.siglaAcademica} - ${docente.unidade.nome}</p>
</div>

<c:set var="todaProducao" value="#{portal.produtividade}" />
<div id="producao-docente">
	<h4>Produção Intelectual</h4>

	<c:if test="${not empty todaProducao}">
	
		<c:set var="coordenacaoGrupoPesquisa" value="#{todaProducao.coordenacaoGrupoPesquisa}"/>
		<c:if test="${not empty coordenacaoGrupoPesquisa}">
		<h2>Coordenação de Bases de Pesquisa (${ fn:length(coordenacaoGrupoPesquisa) })  </h2>
		<ul>
			<c:forEach items="${coordenacaoGrupoPesquisa}" var="item" varStatus="status">
				<li>
				<ufrn:format name="item" property="dataCriacao"
				type="mes_ano_numero" />
				${item.nome},
				${item.repercursoesTrabGrupo}
				</li>
			</c:forEach>
		</ul>
		</c:if>

		<c:set var="relatorioPesquisa" value="#{todaProducao.relatorioPesquisa}"/>
		<c:if test="${not empty relatorioPesquisa}">
		<h2>Relatórios de Projetos de Pesquisa (${ fn:length(relatorioPesquisa) }) </h2>
		<ul>
			<c:forEach items="${relatorioPesquisa}" var="item"	varStatus="status">
					<li>
					${item.codigo.ano}, ${item.titulo}, ${item.centro.sigla}
					</li>
			</c:forEach>
		</ul>
		</c:if>

		<c:set var="coordenacaoProjetoExterno" value="#{todaProducao.coordenacaoProjetoExterno}"/>
		<c:if test="${not empty coordenacaoProjetoExterno}">
		<h2>Coordenação de Projetos de Pesquisa Externos (${ fn:length(coordenacaoProjetoExterno) }) </h2>
		<ul>
			<c:forEach items="${coordenacaoProjetoExterno}" var="item"	varStatus="status">
					<li>
					${item.codigo.ano}, ${item.titulo}, ${item.centro.sigla}
					</li>
			</c:forEach>
		</ul>
		</c:if>

		<c:set var="artigos" value="#{todaProducao.artigos}"/>
		<c:if test="${not empty artigos}">
		<h2>Artigos (${ fn:length(artigos) })</h2>
		<ul>
			<c:forEach items="${artigos}" var="item"
				varStatus="status">
				<li class="${item.classValidacao}">
					${item.anoReferencia}, ${item.autores } , <b>${item.titulo}</b>, ${item.tituloPeriodico} ISSN: ${item.issn }
					<c:if test="${not empty item.idArquivo && item.exibir}">
						<a class="linkArquivo"  href="/sigaa/verProducao?idProducao=${item.idArquivo}&key=${ sf:generateArquivoKey(item.idArquivo) }" target="_blank">
							Download do arquivo
						</a>
					</c:if>
				</li>
			</c:forEach>
		</ul>
		</c:if>


		<c:set var="publicacoes" value="#{todaProducao.publicacoes}" />
		<c:if test="${not empty publicacoes}">
			<h2>Publicação em Eventos (${ fn:length(publicacoes) })</h2>
			<ul>
				<c:forEach items="${publicacoes}" var="item" varStatus="status">
					<li class="${item.classValidacao}">
						${item.anoReferencia}, ${item.autores}, <b>${item.titulo}</b>,	${item.nomeEvento}, ${item.naturezaDesc}
						<c:if test="${not empty item.idArquivo && item.exibir}">
							<a class="linkArquivo"  href="/sigaa/verProducao?idProducao=${item.idArquivo}&key=${ sf:generateArquivoKey(item.idArquivo) }" target="_blank">
								Download do arquivo
							</a>
						</c:if>
					</li>
				</c:forEach>
			</ul>
		</c:if>


		<c:set var="apresentacaoEvento"
			value="#{todaProducao.apresentacaoEvento}" />
		<c:if test="${not empty apresentacaoEvento}">
			<h2>Apresentação em Eventos (${ fn:length(apresentacaoEvento) }) </h2>
			<ul>
				<c:forEach items="${apresentacaoEvento}" var="item" varStatus="status">
					<li class="${item.classValidacao}">
						${item.anoReferencia}, <b>${item.titulo}</b>, ${item.evento},${item.tipoEvento.descricao}
						<c:if test="${not empty item.idArquivo && item.exibir}">
							<a class="linkArquivo"  href="/sigaa/verProducao?idProducao=${item.idArquivo}&key=${ sf:generateArquivoKey(item.idArquivo) }" target="_blank">
								Download do arquivo
							</a>
						</c:if>
					</li>
				</c:forEach>
			</ul>
		</c:if>



		<c:set var="capitulos" value="#{todaProducao.capitulos}" />
		<c:if test="${not empty capitulos}">
		<h2>Capítulos de Livros (${ fn:length(capitulos) }) </h2>
			<ul>
			<c:forEach items="${capitulos}" var="item"
				varStatus="status">
				<li class="${item.classValidacao}">
					${item.anoReferencia}, ${item.titulo}, ${item.tituloLivro},	${item.autores}
					<c:if test="${not empty item.idArquivo && item.exibir}">
						<a class="linkArquivo"  href="/sigaa/verProducao?idProducao=${item.idArquivo}&key=${ sf:generateArquivoKey(item.idArquivo) }" target="_blank">
							Download do arquivo
						</a>
					</c:if>
				</li>
			</c:forEach>
			</ul>
		</c:if>

		<c:set var="livros"	value="#{todaProducao.livro}" />
		<c:if test="${not empty livros}">
		<h2>Livros (${ fn:length(livros) }) </h2>

			<ul>
			<c:forEach items="${livros}" var="item" varStatus="status">
				<li class="${item.classValidacao}">
					${item.anoReferencia}, ${item.titulo}, ${item.autores }
					<c:if test="${not empty item.idArquivo && item.exibir}">
						<a class="linkArquivo"  href="/sigaa/verProducao?idProducao=${item.idArquivo}&key=${ sf:generateArquivoKey(item.idArquivo) }" target="_blank">
							Download do arquivo
						</a>
					</c:if>
				</li>
			</c:forEach>
			</ul>
		</c:if>

		<c:set var="textoDidatico"	value="#{todaProducao.textoDidatico}" />
		<c:if test="${not empty textoDidatico}">
		<h2>Textos Didáticos (${ fn:length(textoDidatico) })</h2>
			<ul>
			<c:forEach items="${textoDidatico}" var="item"
				varStatus="status">
				<li class="${item.classValidacao}">
					${item.anoReferencia}, ${item.titulo}, ${item.autores }
					<c:if test="${not empty item.idArquivo && item.exibir}">
						<a class="linkArquivo"  href="/sigaa/verProducao?idProducao=${item.idArquivo}&key=${ sf:generateArquivoKey(item.idArquivo) }" target="_blank">
							Download do arquivo
						</a>
					</c:if>
				</li>
			</c:forEach>
			</ul>
		</c:if>

		<c:set var="textoDiscussao"	value="#{todaProducao.textoDiscussao}" />
		<c:if test="${not empty textoDiscussao}">
		<h2>Textos para Discussão (${ fn:length(textoDiscussao) })</h2>
			<ul>
			<c:forEach items="${textoDiscussao}" var="item"
				varStatus="status">
				<li class="${item.classValidacao}">
					${item.anoReferencia}, ${item.titulo}, ${item.autores }
					<c:if test="${not empty item.idArquivo && item.exibir}">
						<a class="linkArquivo"  href="/sigaa/verProducao?idProducao=${item.idArquivo}&key=${ sf:generateArquivoKey(item.idArquivo) }" target="_blank">
							Download do arquivo
						</a>
					</c:if>
				</li>
			</c:forEach>
			</ul>
		</c:if>

		<c:set var="producoesArtisticas" value="#{todaProducao.producoesArtisticas}" />
		<c:if test="${not empty producoesArtisticas}">
		<h2>Produção Artistica, Literária e Visual (${ fn:length(producoesArtisticas) })</h2>
			<ul>
				<c:forEach items="${producoesArtisticas}" var="item"
					varStatus="status">
					<li class="${item.classValidacao}"> ${item.anoReferencia}, ${item.titulo}, ${item.tipoArtistico.descricao}, ${item.subTipoArtistico.descricao}, ${item.autores}
						<c:if test="${not empty item.idArquivo && item.exibir}">
							<a class="linkArquivo"  href="/sigaa/verProducao?idProducao=${item.idArquivo}&key=${ sf:generateArquivoKey(item.idArquivo) }" target="_blank">
								Download do arquivo
							</a>
						</c:if>
					</li>
				</c:forEach>
			</ul>
		</c:if>

		<c:set var="producaoTecnologica" value="#{todaProducao.producaoTecnologica}" />
		<c:if test="${not empty producaoTecnologica}">
		<h2>Produções Tecnológicas (${ fn:length(producaoTecnologica) })</h2>
		<ul>
			<c:forEach items="${producaoTecnologica}" var="item" varStatus="status">
				<li class="${item.classValidacao}">
					${item.anoReferencia}, ${item.titulo}, ${item.autores}
					<c:if test="${not empty item.idArquivo && item.exibir}">
						<a class="linkArquivo"  href="/sigaa/verProducao?idProducao=${item.idArquivo}&key=${ sf:generateArquivoKey(item.idArquivo) }" target="_blank">
							Download do arquivo
						</a>
					</c:if>
				</li>
			</c:forEach>
		</ul>
		</c:if>

		<c:set var="bancasConcurso"	value="#{todaProducao.bancasConcurso}" />
		<c:if test="${not empty bancasConcurso}">
		<h2>Participações em Bancas de Concursos (${ fn:length(bancasConcurso) }) </h2>
			<ul>
			<c:forEach items="${bancasConcurso}" var="item" varStatus="status">
				<li class="${item.classValidacao}"> ${item.anoReferencia},  ${item.titulo}, ${item.autor } 
					<c:if test="${not empty item.idArquivo && item.exibir}">
						<a class="linkArquivo"  href="/sigaa/verProducao?idProducao=${item.idArquivo}&key=${ sf:generateArquivoKey(item.idArquivo) }" target="_blank">
							Download do arquivo
						</a>
					</c:if>
				</li>
			</c:forEach>
			</ul>
		</c:if>

		<c:set var="bancasCurso" value="#{todaProducao.bancasCurso}" />
		<c:if test="${not empty bancasCurso}">
		<h2>Participações em Bancas de Cursos (${ fn:length(bancasCurso) })</h2>
			<ul>
			<c:forEach items="${bancasCurso}" var="item" varStatus="status">
				<li class="${item.classValidacao}"> ${item.anoReferencia}, ${item.naturezaExame.descricao }  ${item.titulo}, ${item.autor }
					<c:if test="${not empty item.idArquivo && item.exibir}">
						<a class="linkArquivo"  href="/sigaa/verProducao?idProducao=${item.idArquivo}&key=${ sf:generateArquivoKey(item.idArquivo) }" target="_blank">
							Download do arquivo
						</a>
					</c:if>
				</li>					
			</c:forEach>
			</ul>
		</c:if>

		<c:set var="premioRecebido"	value="#{todaProducao.premioRecebido}" />
		<c:if test="${not empty premioRecebido}">
		<h2>Prêmios Recebidos (${ fn:length(premioRecebido) }) </h2>
			<ul>
			<c:forEach items="${premioRecebido}" var="item"
				varStatus="status">
				<li class="${item.classValidacao}"> ${item.anoReferencia }, ${item.premio}, ${item.tipoRegiao.descricao}
					<c:if test="${not empty item.idArquivo && item.exibir}">
						<a class="linkArquivo"  href="/sigaa/verProducao?idProducao=${item.idArquivo}&key=${ sf:generateArquivoKey(item.idArquivo) }" target="_blank">
							Download do arquivo
						</a>
					</c:if>
				</li>
			</c:forEach>
			</ul>
		</c:if>

		<c:set var="bolsaObtida" value="#{todaProducao.bolsaObtida}" />
		<c:if test="${not empty bolsaObtida}">
		<h2> Bolsas Obtidas (${ fn:length(bolsaObtida) }) </h2>
		<ul>
		<c:forEach items="${bolsaObtida}" var="item" varStatus="status">
				<li class="${item.classValidacao}">
				${item.tipoBolsaProdocente.descricao},
				${item.instituicao },
				<ufrn:format name="item" property="periodoInicio" type="mes_ano_numero" />,
				<ufrn:format name="item" property="periodoFim"	type="mes_ano_numero" />,
				</li>
		</c:forEach>
		</ul>
		</c:if>

		<c:set var="monitoria" value="#{todaProducao.bolsasMonitoria}" />
		<c:if test="${not empty monitoria}">
		<h2> Orientações de Monitoria (${ fn:length(monitoria) }) </h2>
		<ul>
		<c:forEach items="${monitoria}" var="item" varStatus="status">
				<li>
				${item.projetoEnsino.ano},
				${item.discente.pessoa.nome},
				${item.projetoEnsino.titulo}
				</li>
		</c:forEach>
		</ul>
		</c:if>

		<%-- Bolsas de Iniciacao Cientifica
		<c:set var="bolsasIC" value="#{todaProducao.bolsasIC}" />
		<c:if test="${not empty bolsasIC}">
		<h2> Orientações de Iniciação Científica (${ fn:length(bolsasIC) }) </h2>
		<ul>
		<c:forEach items="#{bolsasIC}" var="item" varStatus="status">
				<li>
				${item.planoTrabalho.projetoPesquisa.codigo.ano},
				${item.planoTrabalho.projetoPesquisa.titulo},
				${item.discente.pessoa.nome}
				</li>
		</c:forEach>
		</ul>
		</c:if>
--%>
		<c:set var="visitaCientifica" value="#{todaProducao.visitaCientifica}" />
		<c:if test="${not empty visitaCientifica}">
		<h2>Visitas Científicas (${ fn:length(visitaCientifica) })</h2>
		<table class="tabelaRelatorioBorda" width="100%">
			<ul>
			<c:forEach items="${visitaCientifica}" var="item"
				varStatus="status">
				<li class="${item.classValidacao}"> ${item.anoReferencia }, ${item.titulo}, ${item.local }
					<c:if test="${not empty item.idArquivo && item.exibir}">
						<a class="linkArquivo"  href="/sigaa/verProducao?idProducao=${item.idArquivo}&key=${ sf:generateArquivoKey(item.idArquivo) }" target="_blank">
							Download do arquivo
						</a>
					</c:if>
				</li>
			</c:forEach>
			</ul>
		</table>
		</c:if>

		<c:set var="organizacaoEventos" value="#{todaProducao.organizacaoEventos}"/>
		<c:if test="${not empty organizacaoEventos}">
		<h2>Organização de Eventos, Consultorias, Edição e Revisão de
		Períodicos (${ fn:length(organizacaoEventos) })</h2>
			<ul>
			<c:forEach items="${organizacaoEventos}" var="item" varStatus="status">
				<li class="${item.classValidacao}"> ${item.anoReferencia },${item.tipoParticipacaoOrganizacao.descricao}, ${item.local }, ${item.ambito.descricao}, ${item.veiculo} 
					<c:if test="${not empty item.idArquivo && item.exibir}">
						<a class="linkArquivo"  href="/sigaa/verProducao?idProducao=${item.idArquivo}&key=${ sf:generateArquivoKey(item.idArquivo) }" target="_blank">
							Download do arquivo
						</a>
					</c:if>
				</li>
			</c:forEach>
			</ul>
		</c:if>


		<c:set var="participacaoSociedade" value="#{todaProducao.participacaoSociedade}"/>
		<c:if test="${not empty participacaoSociedade}">
		<h2>Participação em Sociedades Científicas e Culturais (${ fn:length(participacaoSociedade) })</h2>
			<ul>
			<c:forEach items="${todaProducao.participacaoSociedade}" var="item"
				varStatus="status">
				<li class="${item.classValidacao}"> ${item.anoReferencia }, ${item.nomeSociedade}, ${item.tipoParticipacaoSociedade.descricao } 
					<c:if test="${not empty item.idArquivo && item.exibir}">
						<a class="linkArquivo"  href="/sigaa/verProducao?idProducao=${item.idArquivo}&key=${ sf:generateArquivoKey(item.idArquivo) }" target="_blank">
							Download do arquivo
						</a>
					</c:if>
				</li>
			</c:forEach>
			</ul>
		</c:if>

		<c:set var="participacaoColegiado" value="#{todaProducao.participacaoColegiado}"/>
		<c:if test="${not empty participacaoColegiado}">
		<h2>Participação em Colegiados e Comissões (${ fn:length(participacaoColegiado) })</h2>

			<ul>
			<c:forEach items="${participacaoColegiado}" var="item"
				varStatus="status">
				<li class="${item.classValidacao}"> ${item.comissao}, ${item.tipoMembroColegiado.descricao },  ${item.instituicao.nome}
					<c:if test="${not empty item.idArquivo && item.exibir}">
						<a class="linkArquivo"  href="/sigaa/verProducao?idProducao=${item.idArquivo}&key=${ sf:generateArquivoKey(item.idArquivo) }" target="_blank">
							Download do arquivo
						</a>
					</c:if>
				</li>
			</c:forEach>
			</ul>
		</c:if>

		<c:set var="fimCurso" value="#{todaProducao.trabalhoFimCurso}"/>
		<c:if test="${not empty fimCurso}">
		<h2>Trabalho de Fim de Curso(${ fn:length(fimCurso) })</h2>

			<ul>
			<c:forEach items="${fimCurso}" var="item"
				varStatus="status">
				<li> ${item.titulo}, ${item.orientando.pessoa.nome} ${item.orientandoString},
				<ufrn:format name="item" property="dataDefesa" type="mes_ano_numero"/>
				 </li>
			</c:forEach>
			</ul>
		</c:if>

		<c:set var="orientacoes" value="#{todaProducao.todasOrientacoes}"/>
		<c:if test="${not empty orientacoes}">
		<h2>Orientações de Pós-Graduação (${ fn:length(orientacoes) })</h2>

			<ul>
			<c:forEach items="${orientacoes}" var="item"
				varStatus="status">
				<li> ${item.tipoOrientacao.descricao}, ${item.orientando} ${item.orientandoDiscente.pessoa.nome }, <ufrn:format name="item" property="periodoInicio" type="mes_ano_numero"/> - <ufrn:format name="item" property="periodoFim" type="mes_ano_numero"/>,
				<c:if test="${item.dataPublicacao != null}">
					Concluída em <ufrn:format name="item" property="dataPublicacao" type="mes_ano_numero"/>
				</c:if>
				<c:if test="${item.dataPublicacao == null}">
					Orientação em Andamento
				</c:if>
				 </li>
			</c:forEach>
			</ul>
		</c:if>
	</c:if>

	<c:if test="${empty todaProducao}">
		<p class="vazio">
			Produção intelectual não cadastrada
		</p>
	</c:if>
</div>

</f:view>
<%@include file="/public/include/rodape.jsp" %>
