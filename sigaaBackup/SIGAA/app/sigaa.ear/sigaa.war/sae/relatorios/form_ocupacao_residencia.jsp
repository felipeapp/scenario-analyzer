<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema/> > Relatório de Ocupação de Residências</h2>

	<h:form id="form">
		<table class="formulario" style="width: 50%">
			<caption>Informe os critérios para a emissão do relatório</caption>

			<tr>
				<th class="required" width="30%">Ano.Período:</th>
				<td>
					<h:inputText id="ano" value="#{relatoriosSaeMBean.ano}" 
					size="4" maxlength="4" onkeyup="formatarInteiro(this)" />
					.<h:inputText id="semestre" value="#{relatoriosSaeMBean.periodo}" size="1"
					maxlength="1" onkeyup="formatarInteiro(this)" />
				</td>
			</tr>
			
			<tr>
				<th class="required">Residência:</th>
				<td>
					<h:selectOneMenu id="centro" value="#{relatoriosSaeMBean.residencia.id}">
						<f:selectItem itemValue="0" itemLabel="-- Selecione --"  />
						<f:selectItems value="#{residenciaUniversitariaMBean.allCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tfoot>
				<tr>	
					<td colspan="2">
						<h:commandButton action="#{relatoriosSaeMBean.gerarRelatorioOcupacaoResidencia}" value="Emitir Relatório" />
						<h:commandButton action="#{relatoriosSaeMBean.cancelar}" value="Cancelar" immediate="true" 
						 	onclick="return confirm('Deseja cancelar a operação? Todos os dados digitados não salvos serão perdidos!');"> 
						</h:commandButton>
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<div align="center">
			<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>