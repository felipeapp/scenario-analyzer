<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
	.esquerda{text-align: left !important;}
	.direita{text-align: right !important;}
	.centro{text-align: center !important;}
	.instrutores{width: 26%;}
	.nome{width: 27%;}
	.periodo{width: 13%;}
</style>

<f:view>
	
	<a4j:keepAlive beanName="inscricaoAtividadeAP"></a4j:keepAlive>
	<h2 class="title">
		<ufrn:subSistema /> > Detalhes da Atividade de Atualização Pedagógica
	</h2>
			
	<c:if test="${inscricaoAtividadeAP.atividadeVisualizada.idArquivo > 0}">
	<center>
		<div class="infoAltRem">
		    <img src="/shared/img/icones/download.png" style="overflow: visible;"/>: Visualizar Programa
		</div>
	</center>
	</c:if>
	
	<h:form id="formDetalhesAitividade">	
	<table class="visualizacao" width="70%">
		<caption class="visualizacao">Dados da Atividade</caption>

		<tr>
			<th width="50%">Nome:</th>
			<td>
				<h:outputText value="#{inscricaoAtividadeAP.atividadeVisualizada.nome}"/>	
			</td>
		</tr> 
		
		<tr>
			<th>Período:</th>
			<td>
				<h:outputText value="#{inscricaoAtividadeAP.atividadeVisualizada.descricaoPeriodo}" ></h:outputText>	
			</td>
		</tr>
		
		<tr>
			<th>Horário:</th>
			<td>
				<h:outputText value="#{inscricaoAtividadeAP.atividadeVisualizada.horarioInicio} a #{inscricaoAtividadeAP.atividadeVisualizada.horarioFim}" rendered="#{not empty inscricaoAtividadeAP.atividadeVisualizada.horarioInicio}"/>
				<h:outputText value="Não Informado" rendered="#{empty inscricaoAtividadeAP.atividadeVisualizada.horarioInicio}"/>	
			</td>
		</tr>
		
		<tr>
			<th>Carga Horária:</th>
			<td>
				<h:outputText value="#{inscricaoAtividadeAP.atividadeVisualizada.ch}h" rendered="#{not empty inscricaoAtividadeAP.atividadeVisualizada.ch}"/>
				<h:outputText value="Não Informado" rendered="#{empty inscricaoAtividadeAP.atividadeVisualizada.ch}"/>	
			</td>
		</tr>
		
		<tr>
			<th>Nº. de Vagas:</th>
			<td>
				<h:outputText value="#{inscricaoAtividadeAP.atividadeVisualizada.numVagas}" rendered="#{not empty inscricaoAtividadeAP.atividadeVisualizada.numVagas}"/>
				<h:outputText value="Não Informado" rendered="#{empty inscricaoAtividadeAP.atividadeVisualizada.numVagas}"/>	
			</td>
		</tr>
		
		<tr>
			<th>Professores:</th>
			<td>
				<h:outputText value="#{inscricaoAtividadeAP.atividadeVisualizada.descricaoInstrutores}" ></h:outputText>	
			</td>
		</tr>
		
		<tr>
			<th>Arquivo em Anexo:</th>
			<td>
				<c:choose>
					<c:when test="${inscricaoAtividadeAP.atividadeVisualizada.idArquivo > 0}">
						<a href="/sigaa/verProducao?idProducao=${inscricaoAtividadeAP.atividadeVisualizada.idArquivo}&key=${ sf:generateArquivoKey(inscricaoAtividadeAP.atividadeVisualizada.idArquivo) }"
							title="Visualizar Programa" target="_blank">
							<img src="/shared/img/icones/download.png"/>
						</a>
					</c:when>
					<c:otherwise>
						Inexistente.					
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		
		<tr>
			<td class="subListagem" colspan="2">Outras informações:</td>
		</tr>
		<tr>	
			<td colspan="2" valign="top" align="justify">
				<h:outputText escape="false" value="#{inscricaoAtividadeAP.atividadeVisualizada.descricao}" rendered="#{not empty inscricaoAtividadeAP.atividadeVisualizada.descricao}" ></h:outputText>	
				<h:outputText value="Não informado" rendered="#{empty inscricaoAtividadeAP.atividadeVisualizada.descricao}" ></h:outputText>
			</td>
		</tr>
					
		</tbody>
	</table>
		
	<br/>
	<center>
		<h:commandLink styleClass="noborder" value="<< Voltar" title="Voltar" id="btnVoltarInscricao" immediate="true"
				rendered="#{inscricaoAtividadeAP.inscrever}" action="#{inscricaoAtividadeAP.selecionarGrupo}">
				<f:param name="idGrupo" value="#{inscricaoAtividadeAP.atividadeVisualizada.grupoAtividade.id}" />
		</h:commandLink>
		<h:commandLink styleClass="noborder" value="<< Voltar" title="Voltar" id="btnVoltarConsulta" immediate="true"
				rendered="#{!inscricaoAtividadeAP.inscrever}" action="#{inscricaoAtividadeAP.listar}"/>
	</center>
	</h:form>
		
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
