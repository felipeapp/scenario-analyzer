<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<f:view>
	<h2>  <ufrn:subSistema /> > <h:outputText value="#{frequenciaPeriodicosMBean.confirmButton}"/>  Periodicidade</h2>
	<br>
	
	<div class="descricaoOperacao">
		<p>Caro Usu�rio,</p>
		<c:if test="${frequenciaPeriodicosMBean.cadastrando}">
			<p>Informe a descri��o e o tempo de expira��o para a nova periodicidade.</p>  
		</c:if>
		
		<c:if test="${! frequenciaPeriodicosMBean.cadastrando}">
			<p>Altere os dados da periodicidade selecionada.</p> 
		</c:if>
		
		<p>A descri��o identifica a periodicidade no sistema e o tempo de expira��o determina a partir de qual per�odo os fasc�culos com essa periodicidade s�o considerados <i>"n�o correntes"</i>.</p>  
		<p>O tempo de expira��o pode ser informado em MESES ou em ANOS.</p>  
		
	</div>
	
	<a4j:keepAlive beanName="frequenciaPeriodicosMBean"></a4j:keepAlive>
	
	<h:form id="formCadastrarPeriodicidade">
	
		<h:messages showDetail="true" />

		<table class="formulario" width="70%">
			<caption> Dados da <c:if test="${frequenciaPeriodicosMBean.cadastrando}"> Nova </c:if> Periodicidade </caption>
			
			<tr>
				<th class="obrigatorio" style="width: 30%">Descri��o:</th>
				<td colspan="3"><h:inputText value="#{frequenciaPeriodicosMBean.obj.descricao}"  maxlength="100" size="70"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Tempo de Expira��o:</th>
				<td><h:inputText id="tempoDeExpira��o" value="#{frequenciaPeriodicosMBean.obj.tempoExpiracaoInformadoUsuario}" title="Tempo de Expira��o" maxlength="2" size="10" onkeyup="return formatarInteiro(this);"/>
				<ufrn:help>O tempo a partir do qual os fasc�culos da assinatura com essa periodicidade v�o ser considerados "n�o correntes".</ufrn:help>
				</td>
				<th>Unidade do Tempo de Expira��o:</th>
				<td>
					<h:selectOneMenu id="comboUnidadeFrequencia" value="#{frequenciaPeriodicosMBean.valorUnidadeTempoExpiracao}">
						<f:selectItems value="#{frequenciaPeriodicosMBean.unidadesTempoExpiracaoComboBox}" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="4" align="center">
					
						<h:commandButton id="cmdCadastrarFrequencia" value="#{frequenciaPeriodicosMBean.confirmButton}"  action="#{frequenciaPeriodicosMBean.cadastrar}" />
						
						<h:commandButton id="cmdCancelar" value="Cancelar"  action="#{frequenciaPeriodicosMBean.voltar}" immediate="true" onclick="#{confirm}"  />			
						
					</td>
				</tr>
			</tfoot>
		</table>
		
		<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
		
	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>