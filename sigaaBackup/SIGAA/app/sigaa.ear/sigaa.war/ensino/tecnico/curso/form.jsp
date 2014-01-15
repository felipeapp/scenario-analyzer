<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="cursoTecnicoMBean"/>
<h2> <ufrn:subSistema /> &gt; Cadastro de Curso</h2>
	
<h:form id="form">
	<table class="formulario" style="width: 90%">
	  <caption>Dados do Curso</caption>
		<h:inputHidden value="#{cursoTecnicoMBean.obj.id}" />
		<tbody>
			<tr>
				<th>Código no INEP:</th>
				<td><h:inputText value="#{cursoTecnicoMBean.obj.codigoInep}" size="10" maxlength="20" 
					onkeyup="return formatarInteiro(this);"/>(Código do Curso cadastrado no INEP/SETEC)</td>
			</tr>
			<tr>
				<th class="obrigatorio">Código na ${ configSistema['siglaInstituicao'] }:</th>
				<td><h:inputText value="#{cursoTecnicoMBean.obj.codigo}" size="10" maxlength="20"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Nome:</th>
				<td><h:inputText value="#{cursoTecnicoMBean.obj.nome}" size="70" maxlength="70"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Data de Início do Funcionamento: </th>
				<td><t:inputCalendar value="#{cursoTecnicoMBean.obj.dataInicioFuncionamento}"  
					title="Data de Inicio do Funcionamento" size="10" maxlength="10" 
					onkeypress="return(formatarMascara(this,event,'##/##/####'))" 
					id="dataInicioFuncionamento" renderAsPopup="true" 
					renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" >
					<f:converter converterId="convertData"/>
				</t:inputCalendar></td>
			</tr>
			<tr>
				<th class="obrigatorio">Carga Horária Mínima:</th>
				<td><h:inputText value="#{cursoTecnicoMBean.obj.chMinima}" maxlength="5" 
					size="6" onkeyup="formatarInteiro(this);"/>(em horas)</td>
			</tr>
			<tr>
				<th class="obrigatorio">Modalidade: </th>
				<td>
					<h:selectOneMenu value="#{cursoTecnicoMBean.obj.modalidadeCursoTecnico.id}" id="modalidade">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{modalidadeCursoTecnicoMBean.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
			 	<th>Regime Letivo: </th>
			 	<td>
					<h:selectOneMenu value="#{cursoTecnicoMBean.obj.tipoRegimeLetivo.id}" id="tipoRegimeLetivo">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{tipoRegimeLetivo.allCombo}" /> 
					</h:selectOneMenu>
			 	</td>
			</tr>
			<tr>
			 	<th>Sistema Curricular: </th>
			 	<td>
					<h:selectOneMenu value="#{cursoTecnicoMBean.obj.tipoSistemaCurricular.id}" id="sistemaCurricular">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{tipoSistemaCurricular.allCombo}" />
					</h:selectOneMenu>
			 	</td>
			</tr>

			<tr>
			 	<th>Situação do Curso: </th>
			 	<td>
					<h:selectOneMenu value="#{cursoTecnicoMBean.obj.situacaoCursoHabil.id}" id="situacaoCurso">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{situacaoCursoHabil.allCombo}" />
					</h:selectOneMenu>
			 	</td>
			</tr>
			<tr>
			 	<th>Situação do Diploma:</th>
			 	<td>
					<h:selectOneMenu value="#{cursoTecnicoMBean.obj.situacaoDiploma.id}" id="situacaoDiploma">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{situacaoDiploma.allCombo}" />
					</h:selectOneMenu>
			 	</td>
			</tr>
			<tr>
				<th class="obrigatorio">Turno: </th>
			 	<td>
					<h:selectOneMenu value="#{cursoTecnicoMBean.obj.turno.id}" id="turno">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{turno.allCombo}" />
					</h:selectOneMenu>
			 	</td>
			</tr>
			<tr>
				<th class="required">Forma de Participação do Aluno:</th>
				<td>
					<h:selectOneMenu id="modalidadeEducacao" value="#{cursoTecnicoMBean.obj.modalidadeEducacao.id}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{modalidadeEducacao.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Permite alunos com mais de um vínculo ativo?</th>
				<td>
					<h:selectOneRadio id="permitealunosvariosvinculos" value="#{cursoTecnicoMBean.obj.permiteAlunosVariosVinculos}">
						<f:selectItems value="#{cursoTecnicoMBean.simNao}"/>
					</h:selectOneRadio>
				</td>
			</tr>
		</tbody>
		<tfoot>
		   <tr>
				<td colspan="2">
					<h:commandButton value="#{cursoTecnicoMBean.confirmButton}" action="#{cursoTecnicoMBean.cadastrar}" id="cadastrar" />
					<h:commandButton value="Cancelar" action="#{cursoTecnicoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		   </tr>
		</tfoot>
	</table>
	<br/>
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>