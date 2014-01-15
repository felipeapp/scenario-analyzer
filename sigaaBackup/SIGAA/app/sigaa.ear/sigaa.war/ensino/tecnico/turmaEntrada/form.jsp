<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> > Cadastro de Turma de Entrada </h2>
<a4j:keepAlive beanName="turmaEntradaTecnicoMBean"/>
<h:form id="form">
	<table class="formulario" style="width: 80%">
	  <caption>Dados da Turma de Entrada</caption>
		<h:inputHidden value="#{turmaEntradaTecnicoMBean.obj.id}"/>
		<tbody>
			<tr>
				<th width="25%" class="obrigatorio">Curso:</th>
				<td>
					<h:selectOneMenu value="#{turmaEntradaTecnicoMBean.obj.cursoTecnico.id}" id="curso" 
						valueChangeListener="#{turmaEntradaTecnicoMBean.changeCurriculoCurso}" onchange="submit()" immediate="true"> 
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{curso.allCursoTecnicoCombo}" /> 
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th width="25%" class="obrigatorio">Currículo:</th>
				<td>
					<h:selectOneMenu value="#{turmaEntradaTecnicoMBean.obj.estruturaCurricularTecnica.id}" id="curriculo"> 
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{turmaEntradaTecnicoMBean.listaEstCurricularTecnico}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Data de Entrada:</th>
				<td>
					<t:inputCalendar value="#{turmaEntradaTecnicoMBean.obj.dataEntrada}" id="data" size="10" maxlength="10" 
    				  onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
    				  renderAsPopup="true" renderPopupButtonAsImage="true" >
      					<f:converter converterId="convertData"/>
					</t:inputCalendar> 
				</td>	
			</tr>
			<tr>
				<th class="obrigatorio">Ano-Período de Referência:</th>
				<td>
					<h:inputText value="#{turmaEntradaTecnicoMBean.obj.anoReferencia}" size="5" maxlength="4" onkeyup="return formatarInteiro(this);"/> - 
					<h:inputText value="#{turmaEntradaTecnicoMBean.obj.periodoReferencia}" size="2" maxlength="1" onkeyup="return formatarInteiro(this);"/>
				</td>
			</tr>
			<tr>
				<th>Capacidade:</th>
				<td>
					<h:inputText value="#{turmaEntradaTecnicoMBean.obj.capacidade}" size="3" maxlength="4" onkeyup="return formatarInteiro(this);"/> 
				</td>
			</tr>
			<c:if test="${especializacaoTurma.utilizadoPelaGestora}">
				<tr>
					<th>Especialização:</th>
					<td>
						<h:selectOneMenu value="#{turmaEntradaTecnicoMBean.obj.especializacao.id}" id="especializacao"> 
							<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
					 		<f:selectItems value="#{turmaEntradaTecnicoMBean.allEspecializacao}" /> 
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
		</tbody>

		<tr>
		<tfoot>
		   <tr>
				<td colspan="6">
					<h:commandButton value="#{turmaEntradaTecnicoMBean.confirmButton}" action="#{turmaEntradaTecnicoMBean.cadastrar}" id="cadastrar" />
					<h:commandButton value="Cancelar" action="#{turmaEntradaTecnicoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		   </tr>
		</tfoot>
	</table>
	
	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>