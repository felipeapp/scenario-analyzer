<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="cursoGrad"></a4j:keepAlive>
	<h2><ufrn:subSistema /> &gt; Adicionar Reservas em Turma</h2>
	<h:outputText value="#{turmaGraduacaoBean.create}" />
	
	<c:set var="turma" value="${turmaGraduacaoBean.obj}"/>
	<%@include file="/ensino/turma/info_turma.jsp"%>
	
	<h:form id="form">
		<table class="formulario" width="90%">
			<caption>Adicionar Reservas</caption>
			<tbody>
				<tr>
					<th class="obrigatorio"><label for="checkcurso">Curso:</label></th>
					<td>
						<a4j:region>
							<h:selectOneMenu value="#{curriculo.obj.matriz.curso.id}" id="curso"
									valueChangeListener="#{turmaGraduacaoBean.carregaMatrizesCurriculares}">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{turmaGraduacaoBean.cursosGraduacaoCombo}" />
								<a4j:support event="onchange" reRender="matriz" />
							</h:selectOneMenu>
							<a4j:status>
			                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
			            	</a4j:status>
		            	</a4j:region>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Matriz Curricular:</th>
					<td><h:selectOneMenu id="matriz" value="#{turmaGraduacaoBean.reserva.matrizCurricular.id }">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{turmaGraduacaoBean.possiveisMatrizes}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<th class="obrigatorio">Vagas:</th>	
					<td> 
						<h:inputText value="#{turmaGraduacaoBean.reserva.vagasReservadas}"  
							title="Vagas"
							onkeyup="return formatarInteiro(this);" size="3" maxlength="3" 
							converter="#{ shortConverter }" id="Vagas" />
					</td>
				</tr>
				<tr hidden="true">
					<th>Vagas para Ingressantes:</th>	
					<td> 
						<h:inputText value="#{turmaGraduacaoBean.reserva.vagasReservadasIngressantes}"  
							title="Vagas para Ingressantes"
							onkeyup="return formatarInteiro(this);" size="3" maxlength="3" 
							converter="#{ shortConverter }" id="VagasIngressantes" />
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" align="center" class="caixaCinza">
						<h:commandButton value="Adicionar Reserva" action="#{turmaGraduacaoBean.adicionarReservaSemSolicitacao}" id="adicionar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<c:if test="${ empty turmaGraduacaoBean.obj.reservas}">
		<table class="formulario" width="90%">
		<tbody>
			<tr>
				<td style="text-align: center">
					<font color="red"><i><strong>Não há reservas para esta turma.</strong></i></font>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td>
				<h:form id="resumo">
					<h:commandButton value="<< Passo Anterior" action="#{ turmaGraduacaoBean.reservaCursoVoltar }"  id="btVoltar"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ turmaGraduacaoBean.cancelar }" id="btCancelar"/>
					<h:commandButton value="Próximo Passo >>" action="#{ turmaGraduacaoBean.formConfirmacao }" id="btAvancar"/>
				</h:form>
				</td>
			</tr>
		</tfoot>
		</table>
	</c:if>
	<c:if test="${not empty turmaGraduacaoBean.obj.reservas}">
		<br/><br/>
		<center>
		<div class="infoAltRem" style="width:90%">
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />:
			Remover Reserva
		</div>
		</center>
		<table class="formulario" width="90%">
			<caption class="listagem">Reservas desta turma</caption>
			<thead>
				<tr>
					<td rowspan="2" valign="bottom">Curso</td>
					<td rowspan="2" valign="bottom">Turno</td>
					<td rowspan="2" valign="bottom">Grau Acadêmico</td>
					<td rowspan="2" valign="bottom">Habilitação/Ênfase</td>
					<td colspan="3" style="text-align: center;">Reserva de Vagas</td>
					<td rowspan="2" valign="bottom">Solicitante</td>
					<td rowspan="2"></td>
				</tr>
				<tr>
					<td style="text-align: right;">Solicitadas</td>
					<td style="text-align: right;">Reservadas</td>
					<td style="text-align: right;">Ingressantes</td>
				</tr>
			</thead>
			
			<c:forEach items="${turmaGraduacaoBean.obj.reservas}" var="reserva" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td>${reserva.matrizCurricular.curso.descricao}</td>
					<td>${reserva.matrizCurricular.turno.descricao}</td>
					<td>${reserva.matrizCurricular.grauAcademico.descricao}</td>
					<td>
						${reserva.matrizCurricular.habilitacao.nome}
						${reserva.matrizCurricular.enfase.nome}
					</td>
					<td style="text-align: right;">${reserva.vagasSolicitadas}</td>
					<td style="text-align: right;">${reserva.vagasReservadas}</td>
					<td style="text-align: right;">${reserva.vagasReservadasIngressantes}</td>
					<td>${(not turmaGraduacaoBean.obj.ead ? reserva.solicitante : '')}</td>
					<td width="3%">
						<h:form>
							<input type="hidden" value="${reserva.matrizCurricular.id}" name="idMatriz" /> 
							<h:commandButton image="/img/delete.gif" styleClass="noborder" value="Remover Reserva" id="btRemoverReserva" 
							 onclick="#{confirmDelete}" alt="Remover Reserva"  title="Remover Reserva" action="#{turmaGraduacaoBean.removerReservaSemSolicitacao}" />
						</h:form>
					</td>

				</tr>
			</c:forEach>
			<tfoot>
			<tr>
				<td colspan="9">
				<h:form id="resumo">
					<h:commandButton value="<< Passo Anterior" action="#{ turmaGraduacaoBean.reservaCursoVoltar }"  id="btVoltar"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ turmaGraduacaoBean.cancelar }" id="btCancelar"/>
					<h:commandButton value="Próximo Passo >>" action="#{ turmaGraduacaoBean.formConfirmacao }" id="btCadastrar"/>
				</h:form>
				</td>
			</tr>
		</tfoot>
		</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
