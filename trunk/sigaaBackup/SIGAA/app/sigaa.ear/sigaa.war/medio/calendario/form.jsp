<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="calendarioMedioMBean"></a4j:keepAlive>

<f:view>
	<h2 class="title"><ufrn:subSistema /> > Calend�rio Acad�mico</h2>
	
	<div class="descricaoOperacao">
		<p><b>Caro Usu�rio,</b></p><br/>
		<p>Para visualizar ou cadastrar um Calend�rio Acad�mico, informe o Curso que deseja vincul�-lo. 
		Caso n�o for <b>selecionado um Curso</b>, ser� definido um <b>Calend�rio Global</b>, 
		que ser� usado em todos os cursos que n�o possu�rem um calend�rio espec�fico cadastrado.</p>		
	</div>
		
	<h:form id="form">
	<table width="100%" class="formulario">
		<caption>Informe o Curso caso necess�rio</caption>
		<c:if test="${calendarioMedioMBean.obj.unidade.id > 0}">
			<tr>
				<th width="30%" style="font-weight: bold;">Unidade Respons�vel:</th>
				<td>${calendarioMedioMBean.obj.unidade.nome }</td>
			</tr>
		</c:if>
		<tr>
			<th style="font-weight: bold;">N�vel de Ensino:</th>
			<td>${calendarioMedioMBean.obj.nivelDescr}</td>
		</tr>
		<tr>
			<th>Curso:</th>
			<td>			
				<h:selectOneMenu value="#{calendarioMedioMBean.obj.curso.id}" id="curso">
					<f:selectItem itemValue="0" itemLabel="--> TODOS <--" />
					<f:selectItems value="#{cursoMedio.allCombo}" />
				</h:selectOneMenu>
			</td>
		</tr>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Ver Calend�rio" id="confirmar"	action="#{calendarioMedioMBean.iniciarCalendario}" /> 
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{calendarioMedioMBean.cancelar}" id="cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>	
	</h:form>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
