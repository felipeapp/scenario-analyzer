<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<a4j:keepAlive beanName="trancamentoPrograma"/>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Solicitação de Trancamento de Programa</h2>
	
	<c:if test="${not trancamentoPrograma.obj.posteriori }">
		<div class="descricaoOperacao">
			<p style="font-weight:bold">Caro Aluno(a),</p><br/>
			<p>
				Nesta operação você irá solicitar o trancamento do seu programa atual.
				Informe o motivo, e caso o motivo não esteja listado, entre com uma justificativa textual.
				O trancamento valerá apenas para o período letivo atual, e caso haja necessidade, deverá ser renovado a cada novo período.
			</p>
			<br/>
			
			<p style="font-weight:bold;">
				O trancamento de programa só poderá ser efetivado quando todas as seguintes condições forem satisfeitas:
			</p>
			<br/>
			
			<p style="font-weight:bold;"> 
				I- Limite máximo de 4 períodos letivos regulares consecutivos ou não;
			</p>
			<p style="font-weight:bold;">
				II - Solicitado dentro do período de trancamento regular determinado no Calendário Acadêmico definido pela instituição;
			</p>
			<p style="font-weight:bold;">
				III - Caso o discente tenha alguma pendência na biblioteca, será necessário também sua quitação antes da confirmação do trancamento.
			</p>
			
			<c:if test="${trancamentoPrograma.obj.discente.graduacao}">
				<p style="text-align: center; color: red;font-size: 120%">
					<br/>
					<b>A T E N Ç Ã O!</b><br/>
					O trancamento de programa do período ${trancamentoPrograma.obj.ano}.${trancamentoPrograma.obj.periodo} somente será efetivado mediante apresentação presencial ao ${ configSistema['siglaUnidadeGestoraGraduacao'] } do documento comprovante emitido ao fim desta solicitação.
				    <br/>
				 </p>
			 
			</c:if>
		</div>
	</c:if>
	
	<c:if test="${trancamentoPrograma.obj.posteriori }">
			<div class="descricaoOperacao">
			<p style="font-weight:bold">Caro Aluno(a),</p><br/>
			<p>
				Nesta operação você irá solicitar o trancamento do período anterior.
				Informe o motivo, e caso o motivo não esteja listado, entre com uma justificativa textual.
				O trancamento valerá apenas para o período letivo em questão, e caso haja necessidade, deverá ser renovado a cada novo período.
			</p>
			<p style="font-weight:bold;">
				O trancamento a <i>posteriori</i> só poderá ser efetivado quando todas as seguintes condições sejam satisfeitas:
			</p>
			<br/>
			<p style="font-weight:bold;">
				I  - O aluno não tenha conseguido adicionar qualquer carga horária;
			</p>
			<p style="font-weight:bold;">
				II - Não ter sido reprovado por falta e com media 0 (zero) em ao menos um componente curricular no qual estava matriculado;
			</p>
			<p style="font-weight:bold;"> 
				III- Limite máximo de 4 períodos letivos regulares consecutivos ou não;
			</p>
			<p style="font-weight:bold;">
				IV - Dentro do período de trancamento a <i>posteriori</i> determinado no Calendário Acadêmico definido pela instituição.
			</p>
			
			
			<c:if test="${trancamentoPrograma.obj.discente.graduacao}">
				<br/>
				<p style="text-align: center; color: red;font-size: 120%">
					<b>A T E N Ç Ã O!</b><br/>
				    O Trancamento de programa para o período ${trancamentoPrograma.obj.ano}.${trancamentoPrograma.obj.periodo} será confirmado ao final desta operação.
				    <br/>
				 </p>
			 
			</c:if>
			</div>
	</c:if>
	
	
	<h:form id="form">
		<br/>
		
		
				<div align="center">
					<c:if test="${trancamentoPrograma.obj.discente.graduacao && not trancamentoPrograma.obj.posteriori}">
						<h:selectBooleanCheckbox id="estaCienteGrad" value="#{trancamentoPrograma.estaCiente}"/> 
						<label for="form:estaCienteGrad"><b>Estou Ciente</b> que o trancamento só será efetivado mediante <b>Apresentação da Documentação</b> junto ao ${ configSistema['siglaUnidadeGestoraGraduacao'] }.</label>			
					</c:if>
					<c:if test="${trancamentoPrograma.obj.discente.graduacao && trancamentoPrograma.obj.posteriori}">
						<h:selectBooleanCheckbox id="estaCienteGradPosteri" value="#{trancamentoPrograma.estaCiente}"/> 
						<label for="form:estaCienteGradPosteri">Estou ciente das condições acima e desejo prosseguir com a solicitação de trancamento de programa para o período ${trancamentoPrograma.obj.ano}.${trancamentoPrograma.obj.periodo}.</label>			
					</c:if>
					<c:if test="${trancamentoPrograma.obj.discente.stricto}">
						<h:selectBooleanCheckbox id="estaCienteStricto" value="#{trancamentoPrograma.estaCiente}"/> 
						<label for="form:estaCienteStricto"><b>Estou Ciente</b> que o trancamento só será efetivado mediante aprovação da Coordenação do seu curso.</label>							
					</c:if>
				</div>
				<br>
				<div align="center">
					<h:commandButton value="Continuar >>" action="#{trancamentoPrograma.iniciarSolicitacao }"/>
				</div>
	</h:form>
</f:view><%@include file="/WEB-INF/jsp/include/rodape.jsp"%>