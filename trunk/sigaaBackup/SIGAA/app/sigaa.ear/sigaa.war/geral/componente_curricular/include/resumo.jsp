<style>	
	.colCodigo{text-align:center !important}
	.colObg{text-align:center !important}
	.colAtivo{text-align:center !important}
	.colMatriz{text-align:left !important}
	.colPeriodo{text-align:right !important}
	.colCurriculo{text-align:left !important}
</style>	
<h2 class="title">Resumo do Componente Curricular</h2>

<h:form id="form">
	<c:set var="visualizacao" value="${componenteCurricular.reportImpressao ? 'tabelaRelatorio' : 'visualizacao' }" />
	<c:set var="subFormulario" value="${componenteCurricular.reportImpressao ? 'relatorio' : 'subFormulario' }" />
	
	<table class="${visualizacao}" width="100%">
		<c:choose>
			<c:when test="${componenteCurricular.reportImpressao}">
				<thead><tr><th colspan="2"><h3 align="center">Dados Gerais do Componente Curricular</h3></th></tr></thead>
			</c:when>
			<c:otherwise>
				<caption>Dados Gerais do Componente Curricular</caption>
			</c:otherwise>
		</c:choose>
		
		<tr>
			<th>Tipo do Componente Curricular: </th>
			<td><h:outputText value="#{componenteCurricular.obj.tipoComponente.descricao}" /></td>
		</tr>
		<c:if test="${ componenteCurricular.obj.passivelTipoAtividade }">
			<tr>
				<th>Tipo de ${componenteCurricular.obj.atividade ? 'Atividade' : 'Disciplina'}:</th>
				<td><h:outputText value="#{componenteCurricular.obj.tipoAtividade.descricao}" /></td>
			</tr>
			<tr>
				<th>Forma de Participação:</th>
				<td><h:outputText value="#{componenteCurricular.obj.formaParticipacao.descricao}" /></td>
			</tr>				
		</c:if>
		<tr>
			<th>Unidade Responsável:</th>
			<td><h:outputText value="#{componenteCurricular.obj.unidade.nome}" /></td>
		</tr>
		<c:if test="${not empty componenteCurricular.obj.curso.id and componenteCurricular.obj.curso.id > 0}">
			<tr>
				<th>Curso:</th>
				<td><h:outputText value="#{componenteCurricular.obj.curso.descricao}" /></td>
			</tr>
		</c:if>
		<c:if test="${componenteCurricular.obj.curso.id == 0 and componenteCurricular.obj.cursoNovo != null}">
			<tr>
				<th>Curso:</th>
				<td><h:outputText value="#{componenteCurricular.obj.cursoNovo}" /> (Curso Novo)</td>
			</tr>
		</c:if>
		<tr>
			<th width="30%">Código:</th>
			<td><h:outputText value="#{componenteCurricular.obj.codigo}" /></td>
		</tr>
		<tr>
			<th>Nome:</th>
			<td><h:outputText value="#{componenteCurricular.obj.detalhes.nome}" /></td>
		</tr>
		<c:if test="${componenteCurricular.exibeCargaHorariaTotal}">
			<c:if test="${componenteCurricular.exibeCrTeorico}">
				<tr>
					<th>Créditos Teóricos:</th>
					<td><h:outputText value="#{componenteCurricular.obj.detalhes.crAula}" /> crs. (${componenteCurricular.obj.detalhes.chAula} h.) </td>
				</tr>
			</c:if>
			<c:if test="${componenteCurricular.exibeCrPratico}">
				<tr>
					<th>Créditos Práticos:</th>
					<td><h:outputText value="#{componenteCurricular.obj.detalhes.crLaboratorio}" /> crs. (${componenteCurricular.obj.detalhes.chLaboratorio} h.)</td>
				</tr>
			</c:if>
			<c:if test="${componenteCurricular.exibeCrEad}">
				<tr>
					<th>Créditos Ead:</th>
					<td><h:outputText value="#{componenteCurricular.obj.detalhes.crEad}" /> crs. (${componenteCurricular.obj.detalhes.chEad} h.)</td>
				</tr>
			</c:if>
			<c:if test="${componenteCurricular.exibeChTeorico}">
				<tr>
					<th>Carga Horária Teórica:</th>
					<td><h:outputText value="#{componenteCurricular.obj.detalhes.chAula}" /> h.</td>
				</tr>
			</c:if>
			<c:if test="${componenteCurricular.exibeChPratico}">
				<tr>
					<th>Carga Horária Prática:</th>
					<td><h:outputText value="#{componenteCurricular.obj.detalhes.chLaboratorio}" /> h.</td>
				</tr>
			</c:if>
			<c:if test="${componenteCurricular.exibeChEad}">
				<tr>
					<th>Carga Horária de Ead:</th>
					<td><h:outputText value="#{componenteCurricular.obj.detalhes.chEad}" /> h.</td>
				</tr>
			</c:if>
			<c:if test="${componenteCurricular.exibeChDedicadaDocente}">
				<tr>
					<th>Carga Horária Dedicada do Docente:</th>
					<td><h:outputText value="#{componenteCurricular.obj.detalhes.chDedicadaDocente}" /> h.</td>
				</tr>
			</c:if>
		</c:if>
		<tr>
			<th>Carga Horária Total:</th>
			<td>
			<h:outputText value="#{componenteCurricular.obj.chTotal}" /> h.</td>
		</tr>
		<c:if test="${not acesso.programaStricto}">
			<tr>
				<th>Pré-Requisitos:</th>
				<td>
					<sigaa:expressao expr="${componenteCurricular.obj.preRequisito}"/>
				</td>
			</tr>
			<tr>
				<th>Co-Requisitos:</th>
				<td>
					<sigaa:expressao expr="${componenteCurricular.obj.coRequisito}"/>
				</td>
			</tr>
			<tr>
				<th>Equivalências:</th>
				<td>
				<sigaa:expressao expr="${componenteCurricular.obj.equivalencia}"/>
				</td>
			</tr>
		</c:if>
		
		<tr>
			<th>Excluir da Avaliação Institucional:</th>
			<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.excluirAvaliacaoInstitucional}" /></td>
		</tr>
		<c:if test="${componenteCurricular.obj.atividade}">
			<tr>
				<th>Aceita Criar Turma:</th>
				<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.detalhes.atividadeAceitaTurma}" /></td>
			</tr>			
		</c:if>			
		<tr>
			<th>Matriculável On-Line:</th>
			<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.matriculavel}" /></td>
		</tr>
		<tr>
			<th>Horário Flexível da Turma:</th>
			<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.permiteHorarioFlexivel}" /></td>
		</tr>
		<tr>
			<th>Horário Flexível do Docente:</th>
			<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.permiteHorarioDocenteFlexivel}" /></td>
		</tr>
		<tr>
			<th>Obrigatoriedade de ${ (sessionScope.nivel == 'S') ? 'Conceito' : 'Nota Final' }:</th>
			<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.necessitaMediaFinal}" /></td>
		</tr>
		<tr>
			<th>Pode Criar Turma Sem Solicitação:</th>
			<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.turmasSemSolicitacao}" /></td>
		</tr>			
		<tr>
			<th>Necessita de Orientador:</th>
			<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.temOrientador}" /></td>
		</tr>
		<tr>
			<th>Proíbe Aproveitamento:</th>
			<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.detalhes.proibeAproveitamento}" /></td>
		</tr>						
		<tr>
			<th>Possui Subturmas:</th>
			<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.aceitaSubturma}" /></td>
		</tr>
		<tr>
			<th>Exige Horário:</th>
			<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.exigeHorarioEmTurmas}" /></td>
		</tr>
		<c:if test="${componenteCurricular.exibeChCompartilhada}">
			<tr>
				<th>Permite Ch Compartilhada:</th>
				<td><ufrn:format type="bool_sn" valor="${componenteCurricular.obj.detalhes.permiteChCompartilhada}" /></td>
			</tr>
		</c:if>
		<tr>
			<th valign="top">Quantidade de Avaliações:</th>
			<td><h:outputText value="#{componenteCurricular.obj.numUnidades}" /></td>
		</tr>
		<c:if test="${not componenteCurricular.obj.bloco }">
			<tr>
				<th valign="top">Ementa/Descrição:</th>
				<td><h:outputText value="#{componenteCurricular.obj.detalhes.ementa}" /></td>
			</tr>
			<c:if test="${componenteCurricular.exibeBibliografia}">
			<tr>
				<th valign="top">Referências:</th>
				<td><h:outputText value="#{componenteCurricular.obj.bibliografia}" /></td>
			</tr>
			</c:if>
		</c:if>
		<!-- dados do bloco -->
		<c:if test="${componenteCurricular.obj.bloco }">
			<tr>
				<td colspan="2">
				<table class="subFormulario" width="100%">
					<caption>Sub-unidades do Bloco</caption>
					<thead>
						<c:if test="${ not componenteCurricular.tecnico }">
							<td width="7%" style="text-align: right;">Cr</td>
						</c:if>
						<td width="7%" style="text-align: right;">Ch</td>
						<td width="10%">Tipo</td>
						<td>Código</td>
						<td>Nome</td>
					</thead>
					<c:forEach items="#{componenteCurricular.obj.subUnidades}" var="subUnidade" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<c:if test="${not componenteCurricular.tecnico }">
							<td style="text-align: right;">
								<c:if test="${subUnidade.disciplina}">${subUnidade.detalhes.crTotal} crs.</c:if>
							</td>
							</c:if>
							<td style="text-align: right;">
								${subUnidade.detalhes.chTotal}h
							</td>
							<td>${subUnidade.tipoComponente.descricao}</td>
							<td>
								<c:if test="${not empty subUnidade.codigo}">${subUnidade.codigo}</c:if>
								<c:if test="${empty subUnidade.codigo}">A DEFINIR<B><SUP>*</SUP></B></c:if>
							</td>
							<td>${subUnidade.nome}</td>
						</tr>
					</c:forEach>
				</table>
				</td>
			</tr>
		</c:if>
		<c:if test="${not empty componenteCurricular.obj.inversosEquivalentes}">
		<tr>
			<td colspan="2">
				<table class="${subFormulario}" width="100%">
				<caption>Outros componentes que têm esse componente como equivalente</caption>
				<tbody>
				<c:forEach var="c" items="${ componenteCurricular.obj.inversosEquivalentes }" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td>${ c.descricaoResumida}</td>
				</tr>
				</c:forEach>
				</tbody>
				</table>
			</td>
		</tr>
		</c:if>
		<c:if test="${not empty componenteCurricular.obj.inversosPreRequisitos}">
		<tr>
			<td colspan="2">
				<table class="${subFormulario}" width="100%">
				<caption>Outros componentes que têm esse componente como pré-requisito</caption>
				<tbody>
				<c:forEach var="c" items="${ componenteCurricular.obj.inversosPreRequisitos }" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td>${ c.descricaoResumida}</td>
				</tr>
				</c:forEach>
				</tbody>
				</table>
			</td>
		</tr>
		</c:if>
		<c:if test="${not empty componenteCurricular.obj.inversosCoRequisitos}">
		<tr>
			<td colspan="2">
				<table class="${subFormulario}" width="100%">
				<caption>Outros componentes que têm esse componente como co-requisito</caption>
				<tbody>
				<c:forEach var="c" items="${ componenteCurricular.obj.inversosCoRequisitos }" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td>${ c.descricaoResumida}</td>
				</tr>
				</c:forEach>
				</tbody>
				</table>
			</td>
		</tr>
		</c:if>
		
		<c:if test="${not empty componenteCurricular.equivalenciasComponenteEscolhido }">
		<tr>
			<td colspan="2">
				<table class="${subFormulario}" width="100%">
				<caption>Histórico de Equivalências</caption>
				<thead>
					<tr>
						<td class="colMatriz">Expressão de Equivalência</td>
						<td class="colMatriz">Ativa</td>
						<td class="colAtivo">Início da Vigência</td>
						<td class="colAtivo">Fim da Vigência</td>
					</tr>
				</thead>
				<tbody>
				<c:forEach var="e" items="${ componenteCurricular.equivalenciasComponenteEscolhido }" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td class="colMatriz"> <sigaa:expressao expr="${ e[0] }"/></td>
					<td class="colMatriz"> ${ e[1] ? 'Sim' : 'Não' }</td>
					<td class="colAtivo"> <c:if test="${ e[2] != null }"><ufrn:format type="Data" valor="${e[2]}" /></c:if><c:if test="${ e[2] == null }">-</c:if></td>
					<td class="colAtivo"> <c:if test="${ e[3] != null }"><ufrn:format type="Data" valor="${e[3]}" /></c:if><c:if test="${ e[3] == null }">-</c:if></td>
				</tr>
				</c:forEach>
				</tbody>
				</table>
			</td>
		</tr>
		</c:if>
		
		<c:if test="${not empty componenteCurricular.obj.equivalenciaEspecifica}">
		<tr>
			<td colspan="2">
				<table class="${subFormulario}" width="100%">
				<caption>Equivalência(s) Específica(s)</caption>
				<thead>
					<tr>
						<td class="colMatriz">Equivalências Específicas</td>
						<td class="colMatriz">Matriz Curricular</td>
						<td class="colCurriculo">Currículo</td>
						<td class="colAtivo">Início da Vigência</td>
						<td class="colAtivo">Fim da Vigência</td>
					</tr>
				</thead>
				<tbody>
				<c:forEach var="e" items="${ componenteCurricular.obj.equivalenciaEspecifica }" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td class="colMatriz"> <sigaa:expressao expr="${ e.expressao }"/></td>
					<td class="colMatriz"> ${ e.curriculo.matriz.descricao }</td>
					<td class="colCurriculo"> ${ e.curriculo }</td>
					<td class="colAtivo"> <ufrn:format type="Data" valor="${e.inicioVigencia }" /></td>
					<td class="colAtivo"> <ufrn:format type="Data" valor="${e.fimVigencia }" /></td>
				</tr>
				</c:forEach>
				</tbody>
				</table>
			</td>
		</tr>
		</c:if>
		
		<c:if test="${not empty componenteCurricular.obj.inversosEquivalenciaEspecifica}">
		<tr>
			<td colspan="2">
				<table class="${subFormulario}" width="100%">
				<caption>Outros componentes que tem esse componente como equivalência específica</caption>
				<thead>
					<tr>
						<td class="colMatriz">Componente</td>
						<td class="colMatriz">Equivalência Específica</td>
						<td class="colMatriz">Matriz Curricular</td>
						<td class="colCurriculo">Currículo</td>
						<td class="colAtivo">Início da Vigência</td>
						<td class="colAtivo">Fim da Vigência</td>
					</tr>
				</thead>
				<tbody>
				<c:forEach var="e" items="${ componenteCurricular.obj.inversosEquivalenciaEspecifica }" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td class="colMatriz"> ${ e.componente.codigoNome }  </td>
					<td class="colMatriz"> <sigaa:expressao expr="${ e.expressao }"/></td>
					<td class="colMatriz"> ${ e.curriculo.matriz.descricao }</td>
					<td class="colCurriculo"> ${ e.curriculo }</td>
					<td class="colAtivo"> <ufrn:format type="Data" valor="${e.inicioVigencia }" /></td>
					<td class="colAtivo"> <ufrn:format type="Data" valor="${e.fimVigencia }" /></td>
				</tr>
				</c:forEach>
				</tbody>
				</table>
			</td>
		</tr>
		</c:if>
		
		
		<c:if test="${not empty componenteCurricular.obj.expressoesEspecificaCurriculo}">
		<tr>
			<td colspan="2">
				<table class="${subFormulario}" width="100%">
				<caption>Expressões específicas de currículo cadastradas para este componente</caption>
				<thead>
					<tr>
						<td class="colMatriz">Currículo</td>
						<td class="colMatriz">Co-requisito</td>
						<td class="colMatriz">pre-requisito</td>
					</tr>
				</thead>
				<tbody>
				<c:forEach var="e" items="${ componenteCurricular.obj.expressoesEspecificaCurriculo }" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td class="colMatriz"> ${ e.curriculo.descricaoCursoCurriculo }  </td>
					<td class="colMatriz"> <sigaa:expressao expr="${ e.corequisito }"/></td>
					<td class="colMatriz"> <sigaa:expressao expr="${ e.prerequisito }"/></td>
				</tr>
				</c:forEach>
				</tbody>
				</table>
			</td>
		</tr>
		</c:if>
		
	
		<tr>
			<td colspan="2">
		
				<h:dataTable value="#{componenteCurricular.curriculos}" rendered="#{not empty componenteCurricular.curriculos}" 
					id="listaCurriculosComponente" var="cc"  styleClass="#{subFormulario}" columnClasses="colCodigo,colCodigo,colMatriz,colObg,colPeriodo,colAtivo" 
							rowClasses="linhaPar, linhaImpar" width="100%">
						<h:column headerClass="colCodigo">
							<f:facet name="header"><f:verbatim>Código</f:verbatim></f:facet>
							<h:outputText value="#{ cc.curriculo.codigo }"/>
						</h:column>
						
						<h:column headerClass="colCodigo" >
							<f:facet name="header"><f:verbatim>Ano.Período de Implementação</f:verbatim></f:facet>
							<h:outputText value="#{ cc.curriculo.anoEntradaVigor }."/>
							<h:outputText value="#{ cc.curriculo.periodoEntradaVigor }"/>
						</h:column>
						
						<h:column headerClass="colMatriz" >
							<f:facet name="header"><f:verbatim>Matriz Curricular</f:verbatim></f:facet>
							<h:outputText value="#{ cc.curriculo.matriz }"/>
						</h:column>
						
						<h:column headerClass="colObg" >
							<f:facet name="header"><f:verbatim>Obrigatória</f:verbatim></f:facet>
							<h:outputText value="#{ cc.obrigatoria ? 'Sim' : 'Não' }"/>
						</h:column>
						
						<h:column headerClass="colPeriodo" >
							<f:facet name="header"><f:verbatim>Período</f:verbatim></f:facet>
							<h:outputText value="#{ cc.semestreOferta }"/>
						</h:column>
						
						<h:column headerClass="colAtivo" >
							<f:facet name="header"><f:verbatim>Ativo</f:verbatim></f:facet>
							<h:outputText value="#{ cc.curriculo.matriz.ativo ? 'Sim' : 'Não' }"/>
						</h:column>
				</h:dataTable>					

			</td>
		</tr>

		<c:if test="${acesso.cdp or acesso.dae}">
		<c:set var="detalhes" value="${componenteCurricular.todosDetalhes}" />
		<c:if test="${not empty detalhes}">
		<tr>
			<td colspan="2">
				<table class="${subFormulario}" width="100%">
				<caption>Alterações realizadas nesse componente</caption>
				<thead>
				<tr><td width="25%">Data</td><td>Usuário</td></tr>
				</thead>
				<tbody>
				<c:forEach var="det" items="${ componenteCurricular.todosDetalhes }" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td align="center"><ufrn:format type="datahorasec" valor="${det.data}" ></ufrn:format> </td>
					<td>${ det.registroEntrada.usuario.pessoa.nome } (${ det.registroEntrada.usuario.login})</td>
				</tr>
				</c:forEach>
				</tbody>
				</table>
			</td>
		</tr>
		</c:if>
		</c:if>

	</table>
</h:form>