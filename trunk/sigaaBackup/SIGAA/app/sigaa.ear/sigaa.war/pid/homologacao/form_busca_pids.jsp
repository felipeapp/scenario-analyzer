<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema/> > Homologar Planos Individuais do Docente </h2>

	<h:form id="form">
	<table class="formulario" style="width: 60%">
			
			<caption>Informe os crit�rios para a emiss�o do relat�rio</caption>
				<tr>
					<th class="required">Ano-Per�odo:</th>
					<td>
						<h:inputText id="ano" 
							value="#{cargaHorariaPIDMBean.ano}"
							onkeyup="return formatarInteiro(this);" size="4" maxlength="4" /> - 
						
						<h:inputText id="semestre" 
									value="#{cargaHorariaPIDMBean.periodo}" 
									onkeyup="return formatarInteiro(this);" size="2" maxlength="2" />
					</td>
				</tr>
				
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton action="#{cargaHorariaPIDMBean.gerarListagemPIDChefeDepartamento}" value="Emitir Relat�rio" />
							<h:commandButton action="#{cargaHorariaPIDMBean.cancelar}" value="Cancelar" onclick="#{confirm}" immediate="true" />
						</td>
					</tr>
				</tfoot>
	</table>
	
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> <br/>
	</center>
		
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>