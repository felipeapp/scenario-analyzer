<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>

<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<script type="text/javascript">
	function qteCaracteres(){	
		if ($('form:descricaoAtividadesEspecificas').value.length < 500)
			$('txtCaracteresDigitados').innerHTML=$('form:descricaoAtividadesEspecificas').value.length + ' digitados';
		else{
			$('form:descricaoAtividadesEspecificas').value = $('form:descricaoAtividadesEspecificas').value.substr(0,499);
			$('txtCaracteresDigitados').innerHTML= '500 digitados';
		}			
	}
</script>

<rich:panel header="III- OUTRAS ATIVIDADES" styleClass="painelAtividades" id="outrasAtividades">
	
	<div class="descricaoOperacao" style="margin-top: 0;">
		<p>Neste item podem ser informadas outras atividades desenvolvidas em cursos de graduação, de pós-graduação lato e stricto sensu 
		e/ou outros projetos institucionais, com remuneração específica, mediante autorização prévia do CONSEPE.</p>
	</div>	
	
	<div class="esquerda">
		<b> DESCRIÇÃO DA ATIVIDADE</b> 
		<br/>
		<h:inputTextarea id="descricaoAtividadesEspecificas" value="#{cargaHorariaPIDMBean.obj.outrasAtividades}" cols="132" rows="5" onkeyup="qteCaracteres()" 
		onblur="qteCaracteres()"  />
		<br/> <center><i>(500 caracteres/<span id="txtCaracteresDigitados">0 digitados</span> )</i></center> 
	</div>
		
	<p style="font-weight: bold; text-align: left; margin: 5px 0 8px;">				
		<h:selectBooleanCheckbox value="#{cargaHorariaPIDMBean.obj.atividadeAutorizadaCONSEPE}" id="checkBoxAutorizacaoConsepe">
			<f:selectItem itemLabel="Sim" itemValue="true" />
		</h:selectBooleanCheckbox>
		<h:outputLabel for="checkBoxAutorizacaoConsepe">Atesto que esta atividade foi autorizada previamente pelo CONSEPE, como informado na resolução que rege este plano.</h:outputLabel>
	</p>
		
	<center>
		<a4j:commandButton action="#{cargaHorariaPIDMBean.adicionarAtividadeComplementar}" value="Adicionar Atividade" reRender="outrasAtividades"></a4j:commandButton>
	</center>

<c:if test="${ not empty cargaHorariaPIDMBean.ativEspecificasAdicionasDocente}">
	<br>
	<div class="infoAltRem">
		<h:graphicImage value="/img/cronograma/remover.gif" style="overflow: visible;" /> : Remover Atividade
	</div>

	<table class="listagem">
		<tr>
			<td><b>Descrição da Atividade</b></td>
			<td></td>
		</tr>
		<t:dataList value="#{cargaHorariaPIDMBean.ativEspecificasAdicionasDocente}" var="item" rowIndexVar="cont">
           	
           	<t:htmlTag value="tr">
                   <t:htmlTag value="td">
                      		<h:outputText value="#{item.denominacao}" />
                  </t:htmlTag>
               
                   <t:htmlTag value="td" style="text-align: right;">
              			<a4j:commandLink action="#{cargaHorariaPIDMBean.removerAtividadeComplementar}" title = "Remover Atividade" reRender="outrasAtividades, chTotal">
						<f:param name="id" value="#{cont}"/>
						<h:graphicImage value="/img/cronograma/remover.gif"/>
					</a4j:commandLink>
                  </t:htmlTag>
                  
               </t:htmlTag>
               
		</t:dataList>
	</table>
</c:if>
           
</rich:panel>