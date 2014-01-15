<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<f:view>
<a4j:keepAlive beanName="turmaSerieDependencia"/>
<style>
.rich-panel-body {
  padding-bottom:0px;
  padding-left:0px;
  padding-right:0px;
  padding-top:0px;
}
</style>
<%@include file="/medio/turmaSerie/panelCurriculo.jsp"%>
<h2> <ufrn:subSistema /> &gt; ${turmaSerieDependencia.obj.id > 0 ? 'Gerenciamento' : 'Cadastro' } de Turma de Dependência</h2>
	
	<h:form id="form">
		<c:if test="${turmaSerieDependencia.obj.id > 0 && turmaSerieDependencia.cadastroTurmaExistente}">
			<div class="descricaoOperacao" >
				<p>
					A turma de dependência que você esta tentando criar já foi cadastrada anteriormente, no formulário exibido a seguir você pode fazer alterações na mesma:	
				</p>
			</div>
		</c:if>
	<table class="formulario" style="width: 90%">
		<caption>Dados da Turma</caption>
		
		<tbody>
			<tr>
				<th class="obrigatorio" width="40%">Ano:</th>
				<td align="left"><h:inputText value="#{turmaSerieDependencia.obj.ano}" size="5" maxlength="4" id="anoTurma" onkeyup="return formatarInteiro(this);"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Curso:</th>
				<td align="left">
				<c:choose>
				<c:when test="${not turmaSerieDependencia.possuiAlunosMatriculados}">
					<a4j:region>
						<h:selectOneMenu value="#{turmaSerieDependencia.obj.serie.cursoMedio.id}" id="selectCursoTurma"
							valueChangeListener="#{turmaSerieDependencia.carregarSeriesByCurso }" style="width: 70%">
							<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
					 		<f:selectItems value="#{cursoMedio.allCombo}" /> 
					 		<a4j:support event="onchange" reRender="selectSerieTurma,estruturaCurricular,tabelaDisciplinas,componenteCurricular, dataInicioTurma, dataFimTurma" />
						</h:selectOneMenu>
						<a4j:status>
				                <f:facet name="start"><h:graphicImage value="/img/indicator.gif"/></f:facet>
			            </a4j:status>
					</a4j:region>
				</c:when>
				<c:otherwise>
					<h:inputText value="#{turmaSerieDependencia.obj.serie.cursoMedio.nomeCompleto}" disabled="true" id="curso"/>
				</c:otherwise>	
				</c:choose>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Série:</th>
				<td align="left">
				<c:choose>
					<c:when test="${not turmaSerieDependencia.possuiAlunosMatriculados}">
						<a4j:region>
							<h:selectOneMenu value="#{ turmaSerieDependencia.obj.serie.id }" id="selectSerieTurma" onchange="submit()"
								valueChangeListener="#{turmaSerieDependencia.carregarCurriculosBySerieDependencia}" style="width: 70%">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />						
								<f:selectItems value="#{ turmaSerieDependencia.seriesByCurso }" />
								<a4j:support event="onchange" reRender="estruturaCurricular,tabelaDisciplinas,componenteCurricular" />
							</h:selectOneMenu>
							<a4j:status>
					                <f:facet name="start"><h:graphicImage value="/img/indicator.gif"/></f:facet>
				            </a4j:status>
				        </a4j:region>
				    </c:when>
					<c:otherwise>
						<h:inputText value="#{turmaSerieDependencia.obj.serie.descricaoCompleta}" disabled="true" id="serie"/>
					</c:otherwise>	
				</c:choose>    
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Estrutura Curricular:</th>
				<td>
					<c:choose>
						<c:when test="${not turmaSerieDependencia.possuiAlunosMatriculados}">
							<a4j:region>
									<h:selectOneMenu value="#{ turmaSerieDependencia.curriculoSelecionado }" id="estruturaCurricular" onchange="submit()"
									valueChangeListener="#{turmaSerieDependencia.carregarTurmaAnualExistente }" style="width: 70%">
										<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />						
										<f:selectItems value="#{ turmaSerieDependencia.curriculosBySerieDependencia}" />
										<a4j:support event="onchange" reRender="tabelaDisciplinas,componenteCurricular" />										
									</h:selectOneMenu>
									<a4j:status>
							                <f:facet name="start"><h:graphicImage value="/img/indicator.gif"/></f:facet>
						            </a4j:status>
				       		   </a4j:region>								
						</c:when>
						<c:otherwise>
							<h:inputText value="#{turmaSerieDependencia.obj.curriculo.codigo}" id="curriculoTurma" disabled="true" size="45"/>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr>
			<th>
					<h:outputLabel id="componenteCurricular" value="Disciplinas:" styleClass="required" />
			</th>
				<td align="left">
				<rich:panel id="tabelaDisciplinas" style="border-color:#FFF;">
					
						<c:choose>
						<c:when test="${not empty turmaSerieDependencia.curriculoMedio.curriculoComponentes }">
							<table id="tableCurriculos" class="formulario" width="70%" style="margin-left: 0px; padding: 0px;">
							<c:forEach var="linha" items="#{turmaSerieDependencia.curriculoMedio.curriculoComponentes}" varStatus="status">
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}"  >
									<td> 
										<c:if test="${!linha.possuiAlunoMatriculado }">
											<input type="checkbox" name="disciplinas" value="${linha.componente.id}" id="mat${linha.componente.id}" 
											 ${ linha.selecionado ? "checked=\"checked\" ": " " }> 
										</c:if>
										<c:if test="${linha.possuiAlunoMatriculado }">
										<input type="checkbox" name="disciplinas" value="${linha.componente.id}" id="mat${linha.componente.id}" 
											 ${ linha.selecionado ? "checked=\"checked\" ": " " } disabled="true">
										</c:if>
									</td>
									<td>${linha.componente.nome}</td>
									
								</tr>		
							</c:forEach>
							</table>	
						</c:when>
						<c:otherwise>
							Selecione uma Estrutura Curricular Ativa. 
						</c:otherwise>			    
					    </c:choose>
				   
				</rich:panel>    
				</td>
			</tr>
			<tr>
				<th class="obrigatorio" width="50%">Nome:</th>
				<td align="left"><h:inputText value="Dependência" size="10" maxlength="4" id="nomeTurma" onkeyup="CAPS(this)" disabled="true"/></td>
			</tr>
			<tr>
			<th class="obrigatorio">Turno:</th>
				<td>
					<h:selectOneMenu value="#{ turmaSerieDependencia.obj.turno.id }" style="width: 50%;" id="selectTurno">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{ turno.allCombo }" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio" width="50%">Capacidade:</th>
				<td align="left"><h:inputText value="#{turmaSerieDependencia.obj.capacidadeAluno}" size="5" maxlength="4" id="capacidadeTurma" onkeyup="return formatarInteiro(this);"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Início:</th>
				<td>
					<t:inputCalendar value="#{turmaSerieDependencia.obj.dataInicio}"  renderAsPopup="true" size="10" maxlength="10" id="dataInicioTurma"
						popupTodayString="Hoje" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" onkeypress="return(formatarMascara(this,event,'##/##/####'))">
						<f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Fim:<br/>
				</th>
				<td>
					<t:inputCalendar value="#{turmaSerieDependencia.obj.dataFim}"  renderAsPopup="true" size="10" maxlength="10" id="dataFimTurma"
					popupTodayString="Hoje" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" onkeypress="return(formatarMascara(this,event,'##/##/####'))">
						<f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th> Ativo: </th>
				<td>
					<h:selectBooleanCheckbox id="checkAtivo" value="#{turmaSerieDependencia.obj.ativo}" styleClass="noborder" /> 
				</td>
			<tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton value="#{turmaSerieDependencia.confirmButton} Turma" action="#{turmaSerieDependencia.cadastrar}" id="cadastrarTurma" />
					<h:commandButton value="Remover" action="#{turmaSerieDependencia.remover}" rendered="#{turmaSerieDependencia.obj.id > 0}" onclick="return confirm('Deseja realmente remover?')" id="removerTurma" />
					<c:if test="${ turmaSerieDependencia.obj.id > 0 }"><h:commandButton value="<< Voltar" action="#{turmaSerieDependencia.listar}" id="voltar"/>	</c:if>
					<h:commandButton value="Cancelar" action="#{turmaSerieDependencia.cancelar}" onclick="#{confirm}" id="cancelarTurma" />
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