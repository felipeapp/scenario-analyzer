<%@include file="/public/include/cabecalho.jsp"%>
<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<style>
tr.matriz td {
	background: #DEDEDE;
	border-bottom: 1px solid #CCC;
	font-weight: bold;
}

table.visualizacao tr.curriculo td {
	padding-left: 10px;
	font-style: italic;
}
</style>

<f:view>
	<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
	<h2 class="title">
		<h:outputText value="#{idioma.consultaCursos}"/> > <h:outputText value="#{idioma.detalhesCurso}"/>
	</h2>
	
	<h:form id="form">
		<input type="hidden" name="nivel" value="${consultaPublicaCursos.obj.nivel}" />
		
		<table class="visualizacao" style="width: 80%;">
			<caption><h:outputText value="#{idioma.detalhesCurso}"/></caption>
			<tr>
				<th width="30%"><h:outputText value="#{idioma.nivel}" />:</th>
				<td>${consultaPublicaCursos.descricaoNivel}</td>
			</tr>
			<tr>
				<th width="30%"><h:outputText value="#{idioma.curso}" />:</th>
				<td>${consultaPublicaCursos.obj.descricao}</td>
			</tr>
			<tr>
				<th><h:outputText value="#{idioma.unidade}" />:</th>
				<td>${consultaPublicaCursos.obj.unidade.nome}</td>
			</tr>
			<tr>
				<th><h:outputText value="#{idioma.cidade}" />:</th>
				<td>
				${consultaPublicaCursos.obj.municipio.nome}
				<c:if test="${empty consultaPublicaCursos.obj.municipio.nome}">
				<i> <h:outputText value="#{idioma.vazio}"/> </i>
				</c:if>
				</td>
			</tr>
			<tr>
				<th><h:outputText value="#{idioma.modalidadeEnsino}" />:</th>
				<td>
				<c:choose>
					<c:when test="${consultaPublicaCursos.tecnico}">
						${consultaPublicaCursos.obj.modalidadeCursoTecnico.descricao}
					</c:when>
					<c:otherwise>
	               ${consultaPublicaCursos.obj.modalidadeEducacao.descricao}
	            </c:otherwise>
				</c:choose>

				</td>
			</tr>
			<tr>
				<th><h:outputText value="#{idioma.coordenacaoCurso}" />:</th>
				<td>
					<c:set var="coordenacoesGraduacao" value="#{consultaPublicaCursos.obj.coordenacoesCursos}"/>
					<c:choose>
						<c:when test="${not empty coordenacoesGraduacao}">
							<c:forEach items="${coordenacoesGraduacao}"	var="c">
							${c.servidor.nome} (${c.cargoAcademico.descricao})
							<c:if test="${c.servidor.pessoa.email}">
							 	- ${c.servidor.pessoa.email}
							</c:if>
							<br />
							</c:forEach>
						</c:when>
						<c:otherwise>
								<i> <h:outputText value="#{idioma.vazio}"/> </i> 
						</c:otherwise>
					</c:choose>	

					
				 
				</td>
			</tr>
			<tr>
				<th><h:outputText value="#{idioma.telefone}" />(s):</th>
				<td>
				<c:choose>
					<c:when test="${not empty coordenacoesGraduacao}">
						<c:forEach items="${coordenacoesGraduacao}"	var="t">
							${t.telefoneContato1} <c:if test="${not empty t.ramalTelefone1}">- Ramal (${t.ramalTelefone1})</c:if><br />
							${t.telefoneContato2} <c:if test="${not empty t.ramalTelefone2}">- Ramal (${t.ramalTelefone2})</c:if>
						<br />
						</c:forEach>
					</c:when>
					<c:otherwise>
							<i> <h:outputText value="#{idioma.vazio}"/> </i> 
					</c:otherwise>
				</c:choose>	
			</tr>
			<tr>
				<th>E-mail:</th>
				<td>
				<c:choose>
					<c:when test="${not empty coordenacoesGraduacao}">
						<c:forEach items="${coordenacoesGraduacao}"	var="e">
							${e.emailContato} 
						<br />
						</c:forEach>
					</c:when>
					<c:otherwise>
							<i> <h:outputText value="#{idioma.vazio}"/> </i> 
					</c:otherwise>
				</c:choose>	
			</tr>
			<tr>
				<th><h:outputText value="#{idioma.paginaOficial}" />:</th>
				<td>
				<c:choose>
					<c:when test="${not empty coordenacoesGraduacao}">
						<c:forEach items="${coordenacoesGraduacao}"	var="p">
							<a href="${p.paginaOficialCoordenacaoLinkFormatado}" target="_blank">${p.paginaOficialCoordenacao}</a>
						<br />
						</c:forEach>
					</c:when>
					<c:otherwise>
							<i> <h:outputText value="#{idioma.vazio}"/> </i> 
					</c:otherwise>
				</c:choose>	
			</tr>
			<c:if test="${consultaPublicaCursos.stricto}">
			<tr>
				<th><h:outputText value="#{idioma.conceito}" />:</th>
				<td>
					<c:choose>
					<c:when test="${not empty consultaPublicaCursos.recomendacao.conceito}">
						${consultaPublicaCursos.recomendacao.conceito}
					</c:when>
					<c:otherwise>
	               		<i> <h:outputText value="#{idioma.vazio}"/> </i>
	            	</c:otherwise>
				</c:choose>
				</td>
			</tr>
			<tr>
				<th><h:outputText value="#{idioma.portaria}" />:</th>
				<td>
					<c:choose>
					<c:when test="${not empty consultaPublicaCursos.recomendacao.portaria}">
						${consultaPublicaCursos.recomendacao.portaria}
					</c:when>
					<c:otherwise>
	               		<i> <h:outputText value="#{idioma.vazio}"/> </i>
	            	</c:otherwise>
				</c:choose>
				</td>
			</tr>
			</c:if>
			<tr>
				<th><h:outputText value="#{idioma.projetoPoliticoPedagogico}" />:</th>
				<td>
					<c:choose>
					<c:when test="${not empty consultaPublicaCursos.obj.idArquivo}">
						<a class="download"
							href="${ctx}/verProducao?idProducao=${consultaPublicaCursos.obj.idArquivo}&key=${ sf:generateArquivoKey(consultaPublicaCursos.obj.idArquivo) }"
							target="_blank"> <h:outputText value="#{idioma.downloadArquivo}" /> </a>
					</c:when>
					<c:otherwise>
						<i> <h:outputText value="#{idioma.vazio}"/> </i>
					</c:otherwise>
					</c:choose>
					</td>
			</tr>

			<c:if test="${not empty consultaPublicaCursos.curriculos}">
				<tr>
					<td colspan="2"  class="legenda">
						<f:verbatim><h:graphicImage url="/img/view.gif" />:&nbsp;</f:verbatim><h:outputText value="#{idioma.visualizarDetalhesCurriculo}" />
						<f:verbatim><h:graphicImage url="/img/report.png" />:&nbsp;</f:verbatim><h:outputText value="#{idioma.visualizarRelatorioCurriculo}" />
					</td>
				</tr>

				<c:set var="matriz" />
				<tr>
					<td colspan="2">
					<table width="100%" class="subFormulario">
						<caption><h:outputText value="#{idioma.componenteCurricular}"/></caption>

						<c:forEach items="#{consultaPublicaCursos.curriculos}"
							var="curriculoLoop" varStatus="loop">

							<c:if
								test="${not empty curriculoLoop.matriz and matriz != curriculoLoop.matriz.id}">
								<c:set var="matriz" value="${curriculoLoop.matriz.id}" />
								<tr class="matriz">
									<td colspan="2">${curriculoLoop.matriz.descricaoMin}</td>
								</tr>
							</c:if>

							<tr class="curriculo ${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td><h:outputText value="#{idioma.estruturaCurricular}" /> ${curriculoLoop.codigo},
								<h:outputText value="#{idioma.implementadaEm}" />${curriculoLoop.anoPeriodo}</td>
								<td width="10%" align="right"><h:commandLink
									title="Visualizar Estrutura Curricular"
									action="#{consultaPublicaCursos.detalhesCurriculo}">
									<h:graphicImage url="/img/view.gif" />
									<f:param name="id" value="#{curriculoLoop.id}" />
								</h:commandLink>
								<h:commandLink
									title="Relatório da Estrutura Curricular"
									action="#{consultaPublicaCursos.relatorioCurriculo}">
									<h:graphicImage url="/img/report.png" />
									<f:param name="id" value="#{curriculoLoop.id}" />
								</h:commandLink></td>
							</tr>
						</c:forEach>
					</table>
					</td>
				</tr>
			</c:if>

			<c:if test="${consultaPublicaCursos.tecnico}">
				<tr>
					<td colspan="2"><c:if
						test="${not empty consultaPublicaCursos.curriculosTecnico}">
						<table width="100%" class="subFormulario">
							<caption>
								<h:outputText value="#{idioma.curriculo}"/> 
							</caption>
							<c:forEach items="#{consultaPublicaCursos.curriculosTecnico}"
								var="itemLoop" varStatus="loop">
								<tr class="curriculo ${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
									<td>${itemLoop.cursoTecnico.nome}</td>
									<td width="5%"><h:commandLink
										title="Visualizar Curriculo Técnico"
										action="#{consultaPublicaCursos.detalhesCurriculoTecnico}">
										<h:graphicImage url="/img/view.gif" />
										<f:param name="id" value="#{itemLoop.id}" />
									</h:commandLink></td>
								</tr>
							</c:forEach>
						</table>
					</c:if> <c:if test="${empty consultaPublicaCursos.obj.qualificacoes}">
						<table width="100%" class="subFormulario">
							<caption><h:outputText value="#{idioma.qualificacoesTecnicas}"/></caption>
							<tr>
								<td><h:outputText value="#{idioma.vazio}"/></td>
							</tr>
						</table>
					</c:if></td>
				</tr>
			</c:if>

		</table>
	</h:form>
	<br />
	<div class="naoImprimir" style="text-align: center;"><a
		href="javascript: history.go(-1);"> << <h:outputText value="#{idioma.voltar}"/></a></div>


	<br />
</f:view>

<%@include file="/public/include/rodape.jsp"%>