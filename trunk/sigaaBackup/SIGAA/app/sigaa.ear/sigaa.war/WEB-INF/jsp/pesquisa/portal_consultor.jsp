<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	.descricaoOperacao h3 {
		margin-bottom: 20px;
	}

	.aba {
		padding: 5px;
		border: 1px solid #6593CF;
		border-width: 0 1px 1px 1px;
	}

	.vazio {
		text-align: center;
		padding: 10px 20%;
		background: #FDFAF6;
	}

	.avaliacao {
		width: 17%;
		text-align: center;
	}

	span.limite {
		font-weight: bold;
		color: #922;
		font-size: 1.1em;
	}

	span.nota {
		display: block;
		text-align: center;
		font-size: 1.3em;
		font-weight: bold;
	}

	span.data {
		font-style: italic;
	}


	span.recomendado {
		color: #292;
	}

	span.n-recomendado {
		color: #922;
	}
</style>

<h2 class="title">Portal do Consultor</h2>

<div class="descricaoOperacao">
	<p style="margin-bottom: 5px;">
		Caro consultor,
	</p>
	<p>
		Através deste portal você poderá realizar as avaliações dos projetos de pesquisa, planos de trabalho
		e relatórios finais de projeto que a você foram destinados.
	</p>
	<p>
		Para cada projeto será apresentado seu texto completo e ao final um formulário com os itens
		da avaliação da qualidade técnica do projeto, que poderá ser confirmada em uma tela final de resumo.
	</p>
	<%--
	<p>
		<strong>Atenção:</strong> O sistema estará aberto para a realização das avaliações até <span class="limite"> ##/##/#### </span>
	</p>
	--%>
</div>

	<p style="margin: 10px 30%; border: 1px solid #DDD; background: #F5F5F5; text-align: center; padding: 5px">
		<html:link action="/pesquisa/emissaoCertificadoConsultor?dispatch=emitir&obj.id=${sessionScope.usuario.consultor.id}"
			style="background: url(/sigaa/img/certificate.png) no-repeat 15px 50%; line-height: 3em; display: block; />">
			Certificado de Consultoria
		</html:link>
	</p>

<div id="abas-consultor">
	<div id="lista-projetos" class="aba">
	<c:choose>
		<c:when test="${not empty avaliacoesProjeto || not empty projetosEspecial }">
		<table class="listagem">
			<thead>
				<tr>
					<th> Código </th>
					<th> Título </th>
					<th style="text-align: center"> Avaliação </th>
					<th> Avaliar </th>
				</tr>
			</thead>
			<tbody>
			<c:if test="${not empty avaliacoesProjeto}">
				<tr>
					<td colspan="4" class="subFormulario"> Projetos Pendentes de Avaliação e Projetos Avaliados </td>
				</tr>
			</c:if>
				<c:forEach var="avaliacao" items="${avaliacoesProjeto}" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td> ${avaliacao.projetoPesquisa.codigo } </td>
					<td> ${avaliacao.projetoPesquisa.titulo } </td>
					<td class="avaliacao">
						<c:choose>
							<c:when test="${empty avaliacao.dataAvaliacao }">
								PENDENTE
							</c:when>
							<c:otherwise>
								<span class="nota ${ avaliacao.media > 5 ? "recomendado" : "n-recomendado" }">
									<ufrn:format type="decimal" name="avaliacao" property="media"/>
								</span>
								<span class="data"> (em <ufrn:format type="data" name="avaliacao" property="dataAvaliacao" /> ) </span>
							</c:otherwise>
						</c:choose>
					</td>
					<td align="center">
						<ufrn:link action="pesquisa/avaliarProjetoPesquisa" param="obj.id=${avaliacao.id}&dispatch=popular" aba="lista-projetos">
							<img src="${ctx}/img/pesquisa/avaliar.gif" alt="Avaliar Projeto" title="Avaliar Projeto"/>
						</ufrn:link>
					</td>
				</tr>
				</c:forEach>

				<c:if test="${ not empty projetosEspecial }">
					<tr>
						<td colspan="4" class="subFormulario"> Projetos Pendentes de Avaliação (Consultoria Especial) </td>
					</tr>
					<c:forEach var="projeto" items="${projetosEspecial}" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td> ${projeto.codigo } </td>
						<td> ${projeto.titulo } </td>
							<td class="avaliacao">
							<c:set var="media" value="0"/>
							<c:set var="total" value="0"/>
							<c:forEach var="aval" items="${projeto.avaliacoesProjeto}">
								<c:if test="${ not empty aval.dataAvaliacao && empty aval.justificativa }">
									<c:set var="media" value="${media + aval.media}" />
									<c:set var="total" value="${total + 1}"/>
								</c:if>
							</c:forEach>

							<c:choose>
		 						<c:when test="${total > 0}">
									<c:set var="media" value="${ media / total }"/>
									<span class="nota ${ media > 5 ? "recomendado" : "n-recomendado" }">
										<ufrn:format type="decimal" name="media"/>
									</span>
									<span class="data">
										(${total} avaliaç${ total > 1 ? "ões" : "ão" })
									</span>
								</c:when> 
								<c:otherwise>
									PENDENTE
								</c:otherwise>
							</c:choose>
						</td>
						<td align="center">
							<ufrn:link action="pesquisa/avaliarProjetoPesquisa" param="idProjeto=${projeto.id}&dispatch=popularProjeto">
								<img src="${ctx}/img/pesquisa/avaliar.gif" alt="Avaliar Projeto" title="Avaliar Projeto"/>
							</ufrn:link>
						</td>
					</tr>
					</c:forEach>
				</c:if>
			</tbody>
		</table>
		</c:when>
		<c:otherwise>
			<p class="vazio">
				Não foram destinadas avaliações de projetos de pesquisa a você
			</p>
		</c:otherwise>
		</c:choose>
	</div>

	<div id="lista-planos" class="aba">
		<c:choose>
		<c:when test="${not empty planosTrabalho || not empty planosEspecial}">
			<table class="listagem">
				<thead>
					<tr>
						<th> Título </th>
						<th style="text-align: center"> Avaliação </th>
						<th> Avaliar </th>
					</tr>
				</thead>
				<tbody>
				<c:if test="${ not empty planosTrabalho }">
					<tr>
						<td colspan="3" class="subFormulario"> Planos de Trabalho Pendentes de Avaliação </td>
					</tr>
					<c:forEach var="plano" items="${planosTrabalho}" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td> ${ plano.titulo } </td>
						<td align="center">
						    <c:choose>
						    	<c:when test="${plano.status == 3}"> <span class="recomendado"> </c:when>
						    	<c:when test="${plano.status == 4}"> <span class="n-recomendado"> </c:when>
						    	<c:otherwise> <span> </c:otherwise>
						    </c:choose>
							${ plano.statusString }
							</span>
						</td>
						<td align="center">
							<ufrn:link action="/pesquisa/avaliarPlanoTrabalho" param="obj.id=${plano.id}&dispatch=edit" aba="lista-planos">
								<img src="${ctx}/img/pesquisa/avaliar.gif" alt="Avaliar Plano de Trabalho" title="Avaliar Plano de Trabalho"/>
							</ufrn:link>
						</td>
					</tr>
					</c:forEach>
				</c:if>
				<c:if test="${ not empty planosEspecial }">
					<tr>
						<td colspan="3" class="subFormulario"> Planos de Trabalho Pendentes de Avaliação (Consultoria Especial) </td>
					</tr>
					<c:forEach var="plano" items="${planosEspecial}" varStatus="loop">
					<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td> ${ plano.titulo } </td>
						<td align="center">
							<c:choose>
								<c:when test="${ plano.status == 1}">
									PENDENTE
								</c:when>
								<c:otherwise>
									${ plano.statusString }
								</c:otherwise>
							</c:choose>
						 </td>
						<td align="center">
							<ufrn:link action="/pesquisa/avaliarPlanoTrabalho" param="obj.id=${plano.id}&dispatch=edit" aba="lista-planos">
								<img src="${ctx}/img/pesquisa/avaliar.gif" alt="Avaliar Plano de Trabalho" title="Avaliar Plano de Trabalho"/>
							</ufrn:link>
						</td>
					</tr>
					</c:forEach>
				</c:if>
				</tbody>
				</table>
		</c:when>
		<c:otherwise>
			<p class="vazio">
				Não foram destinadas avaliações de planos de trabalho a você
			</p>
		</c:otherwise>
		</c:choose>

	</div>

	<div id="lista-relatorios" class="aba">
		<c:choose>
		<c:when test="${not empty relatorios}">
			<ufrn:table collection="${relatorios}"
				title="Lista de Relatórios para Avaliação"
				properties="projetoPesquisa.codigo, projetoPesquisa.titulo, dataEnvio, statusString"
				headers="Código do Projeto, Nome do Projeto, Data de Envio, Status da Avaliação"
				links="src='${ctx}/img/pesquisa/avaliar.gif',${ctx}/pesquisa/avaliarRelatorioProjeto.do?obj.id={id}&dispatch=edit, Avaliar Relatório" />
		</c:when>
		<c:otherwise>
			<p class="vazio">
				Não foram destinadas avaliações de relatórios de projeto a você
			</p>
		</c:otherwise>
		</c:choose>
	</div>
	
	<div id="lista-relatorios-finais" class="aba">
		<c:choose>
		<c:when test="${not empty relatoriosEspecial}">
			<ufrn:table collection="${relatoriosEspecial}"
				title="Lista de Relatórios Finais de IC para Consulta"
				properties="planoTrabalho.titulo, planoTrabalho.tipoBolsa.descricao, parecerEmitidoString"
				headers="Título do Plano de Trabalho, Tipo de Bolsa, Parecer Emitido?"
				links="src='${ctx}/img/view.gif',${ctx}/pesquisa/relatorioBolsaFinal.do?idRelatorio={id}&dispatch=view, Visualizar Relatório" />
		</c:when>
		<c:otherwise>
			<p class="vazio">
				Não há relatórios finais de bolsa para consulta
			</p>
		</c:otherwise>
		</c:choose>
	</div>
	
		<h3> Auditoria de Projetos e Planos </h3>
		
		<div class="item auditoria">
	 		<html:link action="/pesquisa/planoTrabalho/consulta?dispatch=buscarConsultor&popular=true&aba=iniciacao">Auditar Plano</html:link>
		</div>
		
		<div class="item auditoria">
	 		<h:commandLink value="Auditar Projeto" actionListener="#{pesquisaMBean.redirecionar}">
				<f:param name="url" value="/pesquisa/projetoPesquisa/buscarProjetos.do?dispatch=consulta&popular=true&consulta=true" />
			</h:commandLink>
		</div>
	
</div>

<script>
var Abas = function() {
	return {
	    init : function(){
	        var abas = new YAHOO.ext.TabPanel('abas-consultor');
	        abas.addTab('lista-projetos', "Projetos de Pesquisa (${fn:length(avaliacoesProjeto) + fn:length(projetosEspecial)})")
			abas.addTab('lista-planos', "Planos de Trabalho (${fn:length(planosTrabalho) + fn:length(planosEspecial)})")
			abas.addTab('lista-relatorios', "Relatórios de Projeto (${fn:length(relatorios)})")
			abas.addTab('lista-relatorios-finais', "Relatórios Finais de IC (${fn:length(relatoriosEspecial)})")
			<c:if test="${empty sessionScope.aba}">
	        	abas.activate('lista-projetos');
	    	</c:if>
	    	<c:if test="${sessionScope.aba != null}">
	    		abas.activate('${sessionScope.aba}');
	    	</c:if>
	    }
    }
}();
YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
</script>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>