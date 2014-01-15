<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Importar Aprovados de Vestibulares Externos</h2>

	<div class="descricaoOperacao">
		<p><b>Caro Usuário,</b></p>
		<p>Confira a lista de discentes a ser criado.</p>
	</div>
	<br/>
	<h:form id="form">
	<table class="formulario">
		<caption>
			Lista de Resultados de Aprovação a Importar
		</caption>
		<tr>
			<td colspan="2">
				<table class="formulario" width="100%">
					<c:set var="cursoAnterior" value="" />
					<c:set var="cursoCount" value="0" />
					<c:forEach items="#{importaAprovadosOutrosVestibularesMBean.obj.resultadosOpcaoCursoImportados}" var="item" varStatus="status">
					<c:set var="cursoAtual" value="${item.matrizCurricular.curso.descricao}-${item.matrizCurricular.grauAcademico.descricao}-${item.matrizCurricular.curso.municipio}" />
						<c:if test="${cursoAnterior != cursoAtual}">
							<c:set var="cursoAnterior" value="${item.matrizCurricular.curso.descricao}-${item.matrizCurricular.grauAcademico.descricao}-${item.matrizCurricular.curso.municipio}" />
							<c:set var="cursoCount" value="${ cursoCount + 1 }" />
							<c:if test="${cursoCount > 1}">
										</table>
									</td>
								</tr>
							</c:if>
							<tr>
								<td colspan="3" class="subFormulario">
									<h:outputText value="#{ item.matrizCurricular.curso.descricao }"/> - 
									<h:outputText value="#{ item.matrizCurricular.grauAcademico.descricao }"/> 
									( <h:outputText value="#{ item.matrizCurricular.curso.municipio }" /> )
								</td>
							</tr>
							<tr>
								<td colspan="3" >
									<table class="formulario" width="100%">
										<thead>
											<tr>
												<th>CPF</th>
												<th>Nome</th>
												<th>Status</th>
											</tr>
										</thead>
						</c:if>
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td width="35%"><ufrn:format type="cpf" valor="${item.resultadoClassificacaoCandidato.inscricaoVestibular.pessoa.cpf_cnpj }" /></td>
							<td>${item.resultadoClassificacaoCandidato.inscricaoVestibular.pessoa.nome }</td>
							<td>${item.resultadoClassificacaoCandidato.situacaoCandidato.descricao }</td>
						</tr>
					</c:forEach>
										</table>
									</td>
								</tr>
					</table>
			</td>
		</tr>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Cadastrar" action="#{importaAprovadosOutrosVestibularesMBean.confirmaImportacao}" id="cadastrar"/>
					<h:commandButton value="<< Voltar" action="#{importaAprovadosOutrosVestibularesMBean.formEquivalencia}"  id="voltar"/>
					<h:commandButton value="Cancelar" action="#{importaAprovadosOutrosVestibularesMBean.cancelar}" onclick="#{confirm}" id="cancelar"/>
				</td>
			</tr>
		</tfoot>
	</table>		      
	</h:form>
	<br>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	</center>
	<br>
	<br>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>