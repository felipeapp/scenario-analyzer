<table id="tabela-menu" width="100%">
	<tr>
		<td colspan="2"> <h2>Menu</h2></td>
	</tr>
	<tr>
		<td colspan="2">
			<table style="width:90%" id="icones" align="center">
				<tr>
					<td width="25%" align="center">
						<h:commandLink id="btBoletim" action="#{ portalFamiliar.emitirBoletim }" title="Emitir Boletim">
							<h:graphicImage value="/img/portal_familiar/boletim.png"></h:graphicImage> <br>
							Boletim 
						</h:commandLink> 									
					</td>
					<td width="25%" align="center">
						<h:commandLink id="btObs" action="#{ portalFamiliar.acessarObservacoes }" title="Observações">
							<h:graphicImage value="/img/portal_familiar/obs.png"></h:graphicImage>  <br>
							Observações 
						</h:commandLink> 
					</td>
					<td width="25%" align="center">
						<h:commandLink id="btAddVinculo" action="#{ portalFamiliar.adicionarVinculo }" title="Vincular outro Aluno">
							<h:graphicImage value="/img/icones/user1_preferences.png"></h:graphicImage>  <br>
							Vincular outro Aluno  
						</h:commandLink>
					</td>
					<td width="25%" align="center">
						<h:commandLink id="btAltVinculo" title="Gerenciar outro Aluno" rendered="#{ !empty portalFamiliar.discentes }" onclick="vinculo.show(); return false;" immediate="true">
							<h:graphicImage value="/img/portal_familiar/vinculo.png"></h:graphicImage>  <br>
							Gerenciar outro Aluno  
						</h:commandLink>
					</td>						
				</tr>
			</table>
		</td>
	</tr>
</table>	

<p:dialog widgetVar="vinculo" modal="true" width="500" height="170" header="Gerenciar outro Aluno" resizable="true">

	<h:outputText value="Selecione um dos discentes abaixo para gerencia-lo:" 
		style="font-weight:bold;text-align:center;margin-top:10px;margin-bottom:10px;font-size:10pt;display:block;"/>
	<c:forEach items="#{portalFamiliar.discentes}" var="d" varStatus="status">
		<h:commandLink action="#{portalFamiliar.selecionarDiscente}">
			<f:param name="idDiscente" value="#{d.discente.id}"/>
			<span style="display:inline-block;width:97%;height:16px;overflow:hidden;vertical-align:middle;color:#003390;font-size:9pt;background:url('/sigaa/img/avancar.gif') ${status.index % 2 == 0 ? '#F0F0F0' : ''} no-repeat right;padding:5px;">
				${d.discente.matricula} - ${d.discente.nome}
			</span>
		</h:commandLink>				
	</c:forEach>
				
</p:dialog>				

<br>

<%@include file="discente.jsp" %>

<div id="menu">

	<div class="descricaoOperacao">
	
		<p>Sr(a). ${usuario.nome},</p>
		<br>
		<p>Através deste portal será possível acompanhar o desenvolvimento acadêmico do(s) seu(s) filho(s) ou familiares.</p>
	
	</div>

	<%@include file="proximas_atividades.jsp" %>
	
	<%@include file="disciplinas_matriculadas.jsp" %>
				
</div>

