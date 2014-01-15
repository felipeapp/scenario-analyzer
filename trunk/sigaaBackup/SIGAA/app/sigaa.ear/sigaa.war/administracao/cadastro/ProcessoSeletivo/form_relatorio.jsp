<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	.aprovada {
		color: #292;		
		font-weight: bold;
	}
</style>
<f:view>
<a4j:keepAlive beanName="relatorioInscricaoSelecao"></a4j:keepAlive>
	<h:outputText value="#{relatorioInscricaoSelecao.create}" />
	<h2>
		<ufrn:subSistema /> 
		> Transfer�ncia Volunt�ria > Inscritos
	</h2>

	<h:form id="form">
	<h:messages showDetail="true"></h:messages>

	<div class="descricaoOperacao">
		<p>Caro Usu�rio(a),</p>
		<br />
		<p>	
			<c:choose>
				<c:when test="${relatorioInscricaoSelecao.relInscritos}">
					O relat�rio exibe uma rela��o de todos inscritos de um processo de transfer�ncia volunt�ria, 
					ordenados por MatrizCurricular/Munic�pio.
				</c:when>
				<c:when test="${relatorioInscricaoSelecao.relInscritosAgendados}">
					O relat�rio exibe uma rela��o de todos inscritos de um processo de transfer�ncia volunt�ria 
					,ordenados por Data de Agendamento/Munic�pio, com suas respectivas datas de agendamento e um campo para assinatura. 
				</c:when>
				<c:when test="${relatorioInscricaoSelecao.relQtdInscritos}">
					O relat�rio exibe uma lista das datas que possuem inscritos e a quantidade para cada data. 
				</c:when>
				<c:otherwise>
					O relat�rio exibe uma lista da quantidade de inscritos por cursos em um processo de transfer�ncia volunt�ria. 
				</c:otherwise>
			</c:choose>	  
		</p> 
		<p>
			Selecione o edital desejado e o formato em que deseja gerar o relat�rio. 
		</p>

	</div>

	<table class="formulario" width="55%">
		<caption class="formulario">Dados da Busca</caption>

		<tbody>
	
		
			<tr>
				<td align="right" class="required" width="50%">
					<label>Edital:</label>&nbsp;&nbsp;
				</td>
				<td width="50%">
					<h:selectOneMenu id="idEdital" 
						value="#{relatorioInscricaoSelecao.obj.processoSeletivo.editalProcessoSeletivo.id}" >
						<f:selectItems  value="#{editalProcessoSeletivo.allComboGraduacao}" />
					</h:selectOneMenu>
				</td>
			</tr>
				
		<c:if test="${relatorioInscricaoSelecao.relInscritos}">
			<tr style="align: center;">	
				<td align="right"  class="required"  width="50%">
					<label>Status da Inscri��o:</label>&nbsp;&nbsp;
				</td>
				<td align="left" >
					<h:selectOneMenu value="#{relatorioInscricaoSelecao.statusInscricao}" 
						id="statusInscricao"  >
						<f:selectItems value="#{relatorioInscricaoSelecao.allStatusCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
		</c:if>
			<tr>
				<td align="right"  class="required" width="50%">Exportar no Formato:&nbsp;&nbsp;</td>
				<td align="left">
					<h:selectOneRadio id="jasper"
						value="#{relatorioInscricaoSelecao.formatoJasper}">
						<f:selectItems value="#{relatorioInscricaoSelecao.formatosCombo}" />
					</h:selectOneRadio>
				</td>
			</tr>
		
		
		</tbody>
	
		<tfoot>
			<tr>
				<td colspan="3">
					<h:inputHidden value="1" id="clear" />
					<h:commandButton value="Buscar"  id="btnBuscar"
						action="#{relatorioInscricaoSelecao.gerarRelatorioTransferenciaVoluntaria}" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" id="btnCancelar"
						action="#{relatorioInscricaoSelecao.cancelar}" />
				</td>
			</tr>
		</tfoot>
	</table>

	</h:form>
	<br />
	
	<center>
	  <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/>
	  <span class="fontePequena"> Campos de preenchimento obrigat�rio.</span>
	</center>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>