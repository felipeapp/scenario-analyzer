<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema />
	
	<c:if test="${discenteMedio.obj.id == 0}">
		 >  Cadastro de Aluno ${discenteMedio.discenteAntigo ? 'Antigo' : '' }
	</c:if>
	<c:if test="${discenteMedio.obj.id > 0}">
		 >  Atualizar Aluno 	
	</c:if>
	 
	 </h2>

	<h:form id="form">
	<table class="formulario" style="width: 100%;">
		<caption>Dados do Aluno</caption>
		<tbody>
			<tr>
				<th width="30%"><b>Nome:</b></th>
				<td> ${discenteMedio.obj.nome}</td>
			</tr>
			
			<c:if test="${discenteMedio.discenteAntigo}">
				<tr>
					<th class="obrigatorio"> Matrícula: </th>
					<td>
						<h:inputText id="matricula" size="12" maxlength="12" 
						value="#{ discenteMedio.obj.discente.matricula }" rendered="#{discenteMedio.discenteAntigo}" 
						onkeyup="return formatarInteiro(this);" />
					</td>
				</tr>
			</c:if>
			
			<c:if test="${discenteMedio.obj.id > 0 && not empty discenteMedio.obj.matricula}">
				<tr>
					<th><b> Matrícula:</b></th>
					<td>
						<h:outputText value="#{ discenteMedio.obj.matricula }"/>
					</td>
				</tr>
			</c:if>
			
			<tr>
				<th class="obrigatorio"> Ano de Ingresso: </th>
				<td>
					<h:inputText id="ano" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" 
						value="#{ discenteMedio.obj.anoIngresso }"/>
				</td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Curso de Ingresso:</th>
				<td colspan="5">
				<a4j:region>
					<h:selectOneMenu value="#{discenteMedio.obj.curso.id}" id="selectCurso"
						valueChangeListener="#{discenteMedio.carregarSeriesByCurso }" style="width: 70%">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{curso.allCursoNivelAtualCombo}" /> 
				 		<a4j:support event="onchange" reRender="selectSerie" />
					</h:selectOneMenu>
					<a4j:status>
			            <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
		            </a4j:status>
				</a4j:region>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Série de Ingresso:</th>
				<td>
					<h:selectOneMenu value="#{ discenteMedio.obj.serieIngresso.id }" style="width: 70%;" id="selectSerie">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{ discenteMedio.seriesByCurso }" />
					</h:selectOneMenu>
				</td>
			</tr>		
			
			<tr>
				<th>Escola Anterior:</th>
				<td>
					<h:inputText value="#{discenteMedio.obj.escolaAnterior}" size="60"  id="escolaAnterior" onkeyup="CAPS(this)"/>
				</td>
			</tr>	
			
			<c:if test="${!discenteMedio.discenteAntigo}">
				<tr>
					<th class="obrigatorio">Opção de Turno: </th>
				 	<td>
						<h:selectOneMenu value="#{discenteMedio.obj.opcaoTurno.id}" id="turno" rendered="#{!discenteMedio.discenteAntigo}">
							<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
							<f:selectItems value="#{turno.allCombo}" />
						</h:selectOneMenu>
				 	</td>
				</tr>					
			</c:if>
									
			<c:if test="${discenteMedio.discenteAntigo}">			
				<tr>
					<th class="obrigatorio"> Status:</th>
					
					<td>
						<h:selectOneMenu value="#{discenteMedio.obj.status}" id="status" style="width: 40%;">
							<f:selectItem itemLabel="--> SELECIONE <--" itemValue="0"/>
							<f:selectItems value="#{discenteMedio.statusCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			
			<tr>
				<th class="obrigatorio"> Forma de Ingresso:</th>
				<td>
					<h:selectOneMenu value="#{discenteMedio.obj.formaIngresso.id}" id="formaIngresso" style="width: 40%;">
						<f:selectItem itemLabel="--> SELECIONE <--" itemValue="0"  />
						<f:selectItems value="#{discenteMedio.formasIngressoCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<c:if test="${!discenteMedio.discenteAntigo}">
				<tr>
					<th>Participa do Bolsa Família:</th>
					<td>
						<h:selectOneRadio id="participaBolsaFamilia" value="#{discenteMedio.obj.participaBolsaFamilia}">
							<f:selectItems value="#{discenteMedio.simNao}"  />
						</h:selectOneRadio>
					</td>
				</tr>				
				<tr>
					<th>Utiliza Transporte Escolar Público:</th>
					<td>
						<h:selectOneRadio id="utilizaTransporteEscolarPublico" value="#{discenteMedio.obj.utilizaTransporteEscolarPublico}">
							<f:selectItems value="#{discenteMedio.simNao}"  />
						</h:selectOneRadio>
					</td>
				</tr>
			</c:if>				
		
		</tbody>
		<tfoot>
			<tr><td colspan="2">
				<h:commandButton value="<< Dados Pessoais" action="#{ dadosPessoais.voltarDadosPessoais}" 
					id="btnVoltar" rendered="#{discenteMedio.obj.id == 0}"/>
				<h:commandButton value="Cancelar" action="#{ discenteMedio.cancelar }" onclick="#{confirm}" id="btnCancelar" immediate="true"/>
				<h:commandButton value="Próximo Passo >>" id="Proximo" action="#{ discenteMedio.submeterDadosDiscente }"/>
			</td></tr>
		</tfoot>
	</table>

	<br>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	</center>
	
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>