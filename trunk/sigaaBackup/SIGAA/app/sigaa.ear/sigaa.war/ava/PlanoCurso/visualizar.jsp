<%@include file="/ava/cabecalho.jsp" %>
<f:view>
	<a4j:keepAlive beanName="configuracoesAva" />

	<%@include file="/ava/menu.jsp" %>
	<fieldset>
			<legend>Plano de Curso</legend>
	</fieldset>	
	
			<style> strong { font-weight:bold; } </style>
		
			<div class="descricaoOperacao"><p>Nesta página é possível visualizar o plano de curso definido pelo docente para esta turma.</p></div>
			
			<table class="listagem" style="width:90%;margin-bottom:20px;">
				<caption>Metodologia de Ensino e Avaliação</caption>
				<tr>
					<th style="width:200px;vertical-align:top;font-weight:bold;">Metodologia:</th>
					<td><h:outputText value="#{ planoCurso.obj.metodologia }" /></td>
				</tr>
				<tr>
					<th style="vertical-align:top;font-weight:bold;">Procedimentos de Avaliação da Aprendizagem:</th>
					<td><h:outputText value="#{planoCurso.obj.procedimentoAvalicao}" /></td>
				</tr>
				<tr>
					<th style="vertical-align:top;font-weight:bold;">Horário de atendimento:</th>
					<td><h:outputText value="#{planoCurso.obj.horarioAtendimento}" /></td>
				</tr>
			</table>
			
			<h:dataTable id="listaTopicosAula" value="#{planoCurso.topicosAula}" var="t" columnClasses="data,data,descricao,acao" rowClasses="linhaPar,linhaImpar" styleClass="listagem" style="width:90%;margin-bottom:20px;">
				<f:facet name="caption"><h:outputText value="Cronograma de Aulas" /></f:facet>
				
				<h:column>
					<f:facet name="header">
						<f:verbatim>
							<p align="center"><h:outputText value="Início"/></p>
						</f:verbatim>	
					</f:facet>
					<div style="text-align:center;"><h:outputText value="#{t.data }" /></div>
				</h:column>
				
				<h:column>
					<f:facet name="header">
						<f:verbatim>
							<p align="center"><h:outputText value="Fim"/></p>
						</f:verbatim>		
					</f:facet>
					<div style="text-align:center;"><h:outputText value="#{t.fim }" /></div>
				</h:column>
				
				<h:column>
					<f:facet name="header"><h:outputText value="Descrição"/></f:facet>
					<div style="width:90%;overflow:hidden;height:15px;"><h:outputText value="#{t.descricao }" /></div>
				</h:column>
			</h:dataTable>
				
			
			
			<h:dataTable id="listaAvaliacoes" value="#{planoCurso.avaliacoes}" var="av" columnClasses="data,descricao,acao" rowClasses="linhaPar,linhaImpar" styleClass="listagem" style="width:90%;margin-bottom:20px;">
				<f:facet name="caption"><h:outputText value="Avaliações" /></f:facet>
				
				<h:column>
					<f:facet name="header"><h:outputText value="Data"/></f:facet>
					<h:outputText value="#{av.data }" />
				</h:column>
				
				<h:column>
					<f:facet name="header"><h:outputText value="Descrição"/></f:facet>
					<h:outputText value="#{av.descricao }" style="overflow:hidden;" />
				</h:column>
			</h:dataTable>
			
			<div class="infoAltRem" style="width:90%;"><h:graphicImage value="/img/destaque.png" />: Referência consta na biblioteca</div>
			
			<h:dataTable value="#{planoCurso.referenciasBasicas}" var="r" rowClasses="linhaPar,linhaImpar" styleClass="listagem" style="width:90%;margin-bottom:20px;">
				<f:facet name="caption"><h:outputText value="Referências Básicas" /></f:facet>
				<h:column>
					<f:facet name="header"><h:outputText value="Tipo de material" /></f:facet>
					<h:outputText value="#{r.tipoDesc}"/>
				</h:column>
				<h:column>
					<f:facet name="header"><h:outputText value="Descrição" /></f:facet>
					<h:graphicImage value="/img/destaque.png" rendered="#{ r.tituloCatalografico != null }" />
					<h:outputText value="#{r.descricao}" escape="false" />
				</h:column>
			</h:dataTable>
			
			<h:dataTable value="#{planoCurso.referenciasComplementares}" var="r" rowClasses="linhaPar,linhaImpar" styleClass="listagem" style="width:90%;margin-bottom:20px;">
				<f:facet name="caption"><h:outputText value="Referências Complementares" /></f:facet>
				<h:column>
					<f:facet name="header"><h:outputText value="Tipo de material" /></f:facet>
					<h:outputText value="#{r.tipoDesc}"/>
				</h:column>
				<h:column>
					<f:facet name="header"><h:outputText value="Descrição" /></f:facet>
					<h:graphicImage value="/img/destaque.png" rendered="#{ r.tituloCatalografico != null }" />
					<h:outputText value="#{r.descricao}" escape="false" />
				</h:column>
			</h:dataTable>
				
</f:view>
<%@include file="/ava/rodape.jsp" %>