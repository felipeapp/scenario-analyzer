<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="calendarioMedioMBean"></a4j:keepAlive>

<f:view>
	<h2 class="title"><ufrn:subSistema /> > Calendário Acadêmico</h2>
	
	<div class="descricaoOperacao">
		<p><b>Caro Usuário,</b></p><br/>
		<p>Para visualizar ou cadastrar um Calendário Acadêmico, informe o Curso que deseja vinculá-lo. 
		Caso não for <b>selecionado um Curso</b>, será definido um <b>Calendário Global</b>, 
		que será usado em todos os cursos que não possuírem um calendário específico cadastrado.</p>		
	</div>
		
	<h:form id="form">
	<table width="100%" class="formulario">
		<caption>Informe o Curso caso necessário</caption>
		<c:if test="${calendarioMedioMBean.obj.unidade.id > 0}">
			<tr>
				<th width="30%" style="font-weight: bold;">Unidade Responsável:</th>
				<td>${calendarioMedioMBean.obj.unidade.nome }</td>
			</tr>
		</c:if>
		<tr>
			<th style="font-weight: bold;">Nível de Ensino:</th>
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
					<h:commandButton value="Ver Calendário" id="confirmar"	action="#{calendarioMedioMBean.iniciarCalendario}" /> 
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{calendarioMedioMBean.cancelar}" id="cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>	
	</h:form>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
