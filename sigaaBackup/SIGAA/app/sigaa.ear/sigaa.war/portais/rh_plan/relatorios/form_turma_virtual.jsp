<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2> <ufrn:subSistema/> > Relat�rio de Utiliza��o da Turma Virtual</h2>
	<h:form>
	<h:inputHidden value="#{relatoriosPlanejamento.validaAnoPeriodo}" />
	<h:inputHidden value="#{relatoriosPlanejamento.validaTipoRelatorio}" />
	<h:outputText value="#{relatoriosPlanejamento.create}" />
		<table class="formulario" width="70%">
			<caption class="formulario">Dados da Busca</caption>
			<tbody>
				<tr>
					<td class="required">Tipo de Relat�rio:</td>
					<td>
						<h:selectOneMenu id="tipo_relatorio"
							value="#{relatoriosPlanejamento.tipo}">
							<f:selectItem itemValue="0"
								itemLabel="-- SELECIONE O TIPO DE RELAT�RIO --" />
							<f:selectItem itemValue="1"
								itemLabel="Relat�rio de Utiliza��o da Turma Virtual" />
							<f:selectItem itemValue="2"
								itemLabel="Relat�rio dos Professores que n�o utilizam as turmas virtuais" />
						</h:selectOneMenu>						
					</td>
				</tr>
				<tr align="right">
					<th class="required">Ano-Per�odo:</th>
						<td align="left"><h:inputText value="#{relatoriosPlanejamento.ano}" size="4" maxlength="4" id="ano" onkeyup="return formatarInteiro(this);" />
						- <h:inputText value="#{relatoriosPlanejamento.periodo}" size="1" maxlength="1" id="periodo" onkeyup="return formatarInteiro(this);"/>
						</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Gerar Relat�rio" action="#{relatoriosPlanejamento.relatorioTurmaVirtual}" /> 
						<h:commandButton value="Cancelar" action="#{relatoriosPlanejamento.cancelar}" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>