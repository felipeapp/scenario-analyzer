<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<f:view>
<a4j:keepAlive beanName="turmaSerie"/>
<style>
.rich-panel-body {
  padding-bottom:0px;
  padding-left:0px;
  padding-right:0px;
  padding-top:0px;
}
</style>
<%@include file="/medio/turmaSerie/panelCurriculo.jsp"%>
<h2> <ufrn:subSistema /> &gt; ${turmaSerie.obj.id > 0 ? 'Gerenciamento' : 'Cadastro' } de Turma</h2>
	
	<h:form id="form">
	
	<table class="formulario" style="width: 90%">
		<caption>Dados da Turma</caption>
		
		<tbody>
			<tr>
				<th class="obrigatorio" width="40%">Ano:</th>
				<td align="left"><h:inputText value="#{turmaSerie.obj.ano}" size="5" maxlength="4" id="anoTurma" onkeyup="return formatarInteiro(this);"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Curso:</th>
				<td align="left">
				<c:choose>
				<c:when test="${not turmaSerie.possuiAlunosMatriculados}">
					<a4j:region>
						<h:selectOneMenu value="#{turmaSerie.obj.serie.cursoMedio.id}" id="selectCursoTurma"
							valueChangeListener="#{turmaSerie.carregarSeriesByCurso }" style="width: 70%">
							<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
					 		<f:selectItems value="#{cursoMedio.allCombo}" /> 
					 		<a4j:support event="onchange" reRender="selectSerieTurma,tabela, estruturaCurricular, dataInicioTurma, dataFimTurma" />
						</h:selectOneMenu>
						<a4j:status>
				                <f:facet name="start"><h:graphicImage value="/img/indicator.gif"/></f:facet>
			            </a4j:status>
					</a4j:region>
				</c:when>
				<c:otherwise>
					<h:inputText value="#{turmaSerie.obj.serie.cursoMedio.nomeCompleto}" disabled="true" id="curso"/>
				</c:otherwise>	
				</c:choose>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Série:</th>
				<td align="left">
				<c:choose>
				<c:when test="${not turmaSerie.possuiAlunosMatriculados}">
					<a4j:region>
						<h:selectOneMenu value="#{ turmaSerie.obj.serie.id }" id="selectSerieTurma"
							valueChangeListener="#{turmaSerie.carregarCurriculosBySerie }" style="width: 70%">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{ turmaSerie.seriesByCurso }" />
							<a4j:support event="onchange" reRender="tabela, estruturaCurricular" />
						</h:selectOneMenu>
						<a4j:status>
				                <f:facet name="start"><h:graphicImage value="/img/indicator.gif"/></f:facet>
			            </a4j:status>
			        </a4j:region>
			    </c:when>
				<c:otherwise>
					<h:inputText value="#{turmaSerie.obj.serie.descricaoCompleta}" disabled="true" id="serie"/>
				</c:otherwise>	
				</c:choose>    
				</td>
			</tr>
			<tr>
				<th>
					<h:outputLabel id="estruturaCurricular" value="Estrutura Curricular:" styleClass="required" />
				</th>
				<td>
					<rich:panel id="tabela" style="border-color:#FFF;">
					<c:choose>
					<c:when test="${not turmaSerie.possuiAlunosMatriculados}">
						<c:choose>
						<c:when test="${not empty turmaSerie.curriculosBySerie or turmaSerie.obj.curriculo.id > 0}">
							<table id="tableCurriculos" class="formulario" width="70%" style="margin-left: 0px; padding: 0px;">
								<c:forEach var="linha" items="#{turmaSerie.curriculosBySerie}" varStatus="status">
									<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
										<td width="5%">
											<input type="radio" name="idCurriculo" value="${linha.id}" id="radio${linha.id}" ${ linha.id == turmaSerie.obj.curriculo.id ? "checked=\"checked\" ": " " }/>
										</td>
										<td width="10%">
											<label for="radio${linha.id}">${linha.codigo}</label>
										</td>
										<td align="right">
											<a4j:commandLink id="visualizarLink" title="Visualizar Estrutura Curricular" value="Visualizar Estrutura Curricular" actionListener="#{ turmaSerie.visualizarCurriculo }" reRender="panelCurriculo" oncomplete="Richfaces.showModalPanel('panelCurriculo');">
												<f:param name="idViewCurriculo" value="#{linha.id}"/>
												<h:graphicImage value="/img/biblioteca/emprestimos_ativos.png" style="overflow: visible;" title="Visualizar Estrutura Curricular" />
									        	<rich:componentControl for="panelCurriculo" attachTo="link" operation="show" event="onclick" />
											</a4j:commandLink>
										</td>
									</tr>		
								</c:forEach>
							</table>
						</c:when>
						<c:otherwise>
							Selecione uma Série com Estrutura Curricular Ativa. 
						</c:otherwise>
						</c:choose>	
					</c:when>
					<c:otherwise>
						<h:inputText value="#{turmaSerie.obj.curriculo.codigo}" id="curriculoTurma" disabled="true" size="45"/>
					</c:otherwise>
					</c:choose>
					</rich:panel>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio" width="50%">Nome:</th>
				<td align="left"><h:inputText value="#{turmaSerie.obj.nome}" size="10" maxlength="4" id="nomeTurma" onkeyup="CAPS(this)"/></td>
			</tr>
			<tr>
			<th class="obrigatorio">Turno:</th>
				<td>
					<h:selectOneMenu value="#{ turmaSerie.obj.turno.id }" style="width: 50%;" id="selectTurno">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{ turno.allCombo }" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio" width="50%">Capacidade:</th>
				<td align="left"><h:inputText value="#{turmaSerie.obj.capacidadeAluno}" size="5" maxlength="4" id="capacidadeTurma" onkeyup="return formatarInteiro(this);"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Início:</th>
				<td>
					<t:inputCalendar value="#{turmaSerie.obj.dataInicio}"  renderAsPopup="true" size="10" maxlength="10" id="dataInicioTurma"
						popupTodayString="Hoje" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" onkeypress="return(formatarMascara(this,event,'##/##/####'))">
						<f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Fim:<br/>
				</th>
				<td>
					<t:inputCalendar value="#{turmaSerie.obj.dataFim}"  renderAsPopup="true" size="10" maxlength="10" id="dataFimTurma"
					popupTodayString="Hoje" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" onkeypress="return(formatarMascara(this,event,'##/##/####'))">
						<f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th> Ativo: </th>
				<td>
					<h:selectBooleanCheckbox id="checkAtivo" value="#{turmaSerie.obj.ativo}" styleClass="noborder" /> 
				</td>
			<tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton value="#{turmaSerie.confirmButton} Turma" action="#{turmaSerie.cadastrar}" id="cadastrarTurma" />
					<h:commandButton value="Remover" action="#{turmaSerie.remover}" rendered="#{turmaSerie.obj.id > 0}" onclick="return confirm('Deseja realmente remover?')" id="removerTurma" />
					<c:if test="${ turmaSerie.obj.id > 0 }"><h:commandButton value="<< Voltar" action="#{turmaSerie.listar}" id="voltar"/>	</c:if>
					<h:commandButton value="Cancelar" action="#{turmaSerie.cancelar}" onclick="#{confirm}" id="cancelarTurma" />
				</td>
			</tr>
		</tfoot>
	</table>		
	<br/>
	 <center>
		 <h:graphicImage url="/img/required.gif"/>
		 <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	 </center>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>