<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="title"><ufrn:subSistema /> > Cadastro de Cursos</h2>

	<table class="visualizacao" width="99%">
		<caption class="formulario">Dados do Curso</caption>
		<tr>
			<th width="35%">Nome:</th>
			<td><h:outputText id="nome" value="#{cursoGrad.obj.nome}" /></td>
		</tr>
		<c:if test="${cursoGrad.stricto}">
			<tr>
				<th>Tipo Stricto:</th>
				<td><h:outputText id="tipoStricto"
					value="#{cursoGrad.obj.tipoCursoStricto.descricao}" /></td>
			</tr>
			<tr>
				<th>Portaria MEC:</th>
				<td>
					<c:if test="${ not empty cursoGrad.obj.reconhecimentoPortaria}">
						<h:outputText id="portalMec" value="#{cursoGrad.obj.reconhecimentoPortaria}" />
					</c:if>
					<c:if test="${ empty cursoGrad.obj.reconhecimentoPortaria}">
						-
					</c:if>
				</td>
			</tr>
			<tr>
				<th>Data de Publicação:</th>
				<td>
					<c:if test="${ not empty cursoGrad.obj.dou}">
						<h:outputText id="dataPublicacao" value="#{cursoGrad.obj.dou}" />
					</c:if>			
					<c:if test="${ empty cursoGrad.obj.dou}">
						-
					</c:if>
				</td>
			</tr>
			<tr>
				<th>Data de Indício de Funcionamento:</th>
				<td>
					<c:if test="${ not empty cursoGrad.obj.dataDecreto}">
						<h:outputText id="dataInicioFuncionamento" value="#{cursoGrad.obj.dataDecreto}" />
					</c:if>
					<c:if test="${ empty cursoGrad.obj.dataDecreto}">
						-
					</c:if>
				</td>
			</tr>
			<tr>
				<th>Titulação:</th>
				<td>
					<h:outputText id="titulacao" value="#{cursoGrad.obj.titulacao}"/>
				</td>
			</tr>
			<tr>
				<th>Código CAPES do Curso:</th>
				<td><h:outputText id="codCapes" value="#{cursoGrad.obj.codigo}" /></td>
			</tr>
			<tr>
				<th>Código CAPES do Programa:</th>
				<td><h:outputText id="codCapesProg"
					value="#{cursoGrad.obj.codProgramaCAPES}" /></td>
			</tr>
			<tr>
				<th>Organização Administrativa:</th>
				<td><h:outputText id="organizacaoAdministrativa"
					value="#{cursoGrad.obj.organizacaoAdministrativa.denominacao}" /></td>
			</tr>
			<tr>
				<th>${cursoGrad.stricto ? 'Regimento do Curso' : 'Projeto Político-Pedagógico'}:</th>
				<td>
				<c:choose>
					<c:when test="${not empty cursoGrad.idArquivo && cursoGrad.idArquivo > 0}">
						<a href="${ctx}/verProducao?idArquivo=${ cursoGrad.idArquivo}&key=${ sf:generateArquivoKey(cursoGrad.idArquivo) }"
							target="_blank">Clique aqui para ver o arquivo <h:graphicImage
							value="/img/icones/document_view.png" style="vertical-align: middle; overflow: visible;" /></a>
					</c:when>
					<c:otherwise>
						<i><h:outputText value="Não há arquivo disponível" /></i>
					</c:otherwise>
				</c:choose>
				</td>
			</tr>
		</c:if>	
		
		<c:if test="${!cursoGrad.stricto}">
			<tr>
				<th>Código INEP:</th>
				<td><h:outputText id="codigoINEP"
					value="#{cursoGrad.obj.codigoINEP}" /></td>
			</tr>
		</c:if>

		<tr>
			<th>Município de Andamento do Curso:</th>
			<td><h:outputText id="municipio"
				value="#{cursoGrad.obj.municipio.nome}" /></td>
		</tr>
		<tr>
			<th>Área do Curso:</th>
			<td><h:outputText id="area"
				value="#{cursoGrad.obj.areaCurso.nome}" /></td>
		</tr>

		<tr>
			<th>Forma de Participação do Aluno:</th>
			<td><h:outputText id="modalidade"
				value="#{cursoGrad.obj.modalidadeEducacao.descricao}" /></td>
		</tr>

		<c:if test="${!cursoGrad.stricto}">
			<tr>
				<th>Área de Conhecimento do Vestibular:</th>
				<td><h:outputText id="areaVestibular"
					value="#{cursoGrad.obj.areaVestibular.descricao}" /></td>
			</tr>
			<tr>
				<th>Natureza do Curso:</th>
				<td><h:outputText id="natureza"
					value="#{cursoGrad.obj.naturezaCurso.descricao}" /></td>
			</tr>
		</c:if>
		<tr>
			<th>${cursoGrad.stricto ? 'Periodicidade de Ingresso' : 'Tipo de
			Oferta do Curso'}:</th>
			<td><h:outputText id="tipoOfertaCurso"
				value="#{cursoGrad.obj.tipoOfertaCurso.descricao}" /></td>
		</tr>
		<tr>
			<th>Tipo de Oferta de Disciplina:</th>
			<td><h:outputText id="tipoOfertaDisciplina"
				value="#{cursoGrad.obj.tipoOfertaDisciplina.denominacao}" /></td>
		</tr>
		<tr>
			<th>Tipo de Ciclo de Formação:</th>
			<td><h:outputText id="tipoCicloFormacao"
				value="#{cursoGrad.obj.tipoCicloFormacao.descricao}" /></td>
		</tr>
		<tr>
			<th>Convênio Acadêmico:</th>
			<td><h:outputText id="convenio"
				value="#{cursoGrad.obj.convenio.descricao}" /></td>
		</tr>
		<c:if test="${not cursoGrad.readOnly }">
			<tr>
				<th>Unidade Responsável:</th>
				<td><h:outputText id="unidade"
					value="#{cursoGrad.obj.unidade.nome}" /></td>
			</tr>
			<tr>
				<th>Unidade Responsável 2:</th>
				<td><h:outputText id="unidade2"
					value="#{cursoGrad.obj.unidade2.nome}" /></td>
			</tr>
		</c:if>
		<c:if test="${not cursoGrad.stricto}">
			<tr>
				<th>Unidade da Coordenação:</th>
				<td><h:outputText id="unidadeCoordenacao"
					value="#{cursoGrad.obj.unidadeCoordenacao.nome}" /></td>
			</tr>
		</c:if>
		
		<tr>
			<th>Coordenador Pode Matricular Discente:</th>
			<td><h:outputText value="#{cursoGrad.obj.podeMatricular}" converter="convertSimNao"/></td>	
		</tr>
		
		<tr>
			<th>Ativo:</th>
			<td><h:outputText value="#{cursoGrad.obj.ativo}" converter="convertSimNao"/></td>
		</tr>
		
		<c:if test="${not cursoGrad.stricto}">
			<tr>
				<td colspan="2">
					<table width="100%" class="subFormulario">
						<caption >Projeto Político-Pedagógico</caption>
						<tr>
							<th>${cursoGrad.stricto ? 'Regimento do Curso' : 'Projeto Político-Pedagógico'}:</th>
							<td>
							<c:choose>
								<c:when test="${not empty cursoGrad.idArquivo && cursoGrad.idArquivo > 0}">
									<a href="${ctx}/verProducao?idArquivo=${ cursoGrad.idArquivo}&key=${ sf:generateArquivoKey(cursoGrad.idArquivo) }"
										target="_blank">Clique aqui para ver o arquivo <h:graphicImage
										value="/img/icones/document_view.png" style="vertical-align: middle; overflow: visible;" /></a>
								</c:when>
								<c:otherwise>
									<i><h:outputText value="Não há arquivo disponível" /></i>
								</c:otherwise>
							</c:choose>
							</td>
						</tr>
						<tr>
							<th width="35%" valign="top">Perfil do Profissional:</th>
							<td>${cursoGrad.obj.perfilProfissional}</td>
						</tr>
						<tr>
							<th width="35%" valign="top">Área de Atuação:</th>
							<td>${cursoGrad.obj.campoAtuacao}</td>
						</tr>
						<tr>
							<th width="35%" valign="top">Competências e Habilidades do Profissional:</th>
							<td>${cursoGrad.obj.competenciasHabilidades}</td>
						</tr>
						<tr>
							<th width="35%" valign="top">Metodologia:</th>
							<td>${cursoGrad.obj.metodologia}</td>
						</tr>
						<tr>
							<th width="35%" valign="top">Sistema de Gestão do Curso:</th>
							<td>${cursoGrad.obj.gestaoCurso}</td>
						</tr>
						<tr>
							<th width="35%" valign="top">Avaliação do Curso:</th>
							<td>${cursoGrad.obj.avaliacaoCurso}</td>
						</tr>
						
					</table>
				</td>
			</tr>
		</c:if>
	</table>

	<br>
	<center><a href="javascript: history.go(-1);"><< Voltar</a></center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
