<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<a4j:keepAlive beanName="trancamentoPrograma"/>

<f:view>
	<h2> <ufrn:subSistema /> &gt; Solicita��o de Trancamento de Programa</h2>
	
	<c:if test="${not trancamentoPrograma.obj.posteriori }">
		<div class="descricaoOperacao">
			<p style="font-weight:bold">Caro Aluno(a),</p><br/>
			<p>
				Nesta opera��o voc� ir� solicitar o trancamento do seu programa atual.
				Informe o motivo, e caso o motivo n�o esteja listado, entre com uma justificativa textual.
				O trancamento valer� apenas para o per�odo letivo atual, e caso haja necessidade, dever� ser renovado a cada novo per�odo.
			</p>
			<br/>
			
			<p style="font-weight:bold;">
				O trancamento de programa s� poder� ser efetivado quando todas as seguintes condi��es forem satisfeitas:
			</p>
			<br/>
			
			<p style="font-weight:bold;"> 
				I- Limite m�ximo de 4 per�odos letivos regulares consecutivos ou n�o;
			</p>
			<p style="font-weight:bold;">
				II - Solicitado dentro do per�odo de trancamento regular determinado no Calend�rio Acad�mico definido pela institui��o;
			</p>
			<p style="font-weight:bold;">
				III - Caso o discente tenha alguma pend�ncia na biblioteca, ser� necess�rio tamb�m sua quita��o antes da confirma��o do trancamento.
			</p>
			
			<c:if test="${trancamentoPrograma.obj.discente.graduacao}">
				<p style="text-align: center; color: red;font-size: 120%">
					<br/>
					<b>A T E N � � O!</b><br/>
					O trancamento de programa do per�odo ${trancamentoPrograma.obj.ano}.${trancamentoPrograma.obj.periodo} somente ser� efetivado mediante apresenta��o presencial ao ${ configSistema['siglaUnidadeGestoraGraduacao'] } do documento comprovante emitido ao fim desta solicita��o.
				    <br/>
				 </p>
			 
			</c:if>
		</div>
	</c:if>
	
	<c:if test="${trancamentoPrograma.obj.posteriori }">
			<div class="descricaoOperacao">
			<p style="font-weight:bold">Caro Aluno(a),</p><br/>
			<p>
				Nesta opera��o voc� ir� solicitar o trancamento do per�odo anterior.
				Informe o motivo, e caso o motivo n�o esteja listado, entre com uma justificativa textual.
				O trancamento valer� apenas para o per�odo letivo em quest�o, e caso haja necessidade, dever� ser renovado a cada novo per�odo.
			</p>
			<p style="font-weight:bold;">
				O trancamento a <i>posteriori</i> s� poder� ser efetivado quando todas as seguintes condi��es sejam satisfeitas:
			</p>
			<br/>
			<p style="font-weight:bold;">
				I  - O aluno n�o tenha conseguido adicionar qualquer carga hor�ria;
			</p>
			<p style="font-weight:bold;">
				II - N�o ter sido reprovado por falta e com media 0 (zero) em ao menos um componente curricular no qual estava matriculado;
			</p>
			<p style="font-weight:bold;"> 
				III- Limite m�ximo de 4 per�odos letivos regulares consecutivos ou n�o;
			</p>
			<p style="font-weight:bold;">
				IV - Dentro do per�odo de trancamento a <i>posteriori</i> determinado no Calend�rio Acad�mico definido pela institui��o.
			</p>
			
			
			<c:if test="${trancamentoPrograma.obj.discente.graduacao}">
				<br/>
				<p style="text-align: center; color: red;font-size: 120%">
					<b>A T E N � � O!</b><br/>
				    O Trancamento de programa para o per�odo ${trancamentoPrograma.obj.ano}.${trancamentoPrograma.obj.periodo} ser� confirmado ao final desta opera��o.
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
						<label for="form:estaCienteGrad"><b>Estou Ciente</b> que o trancamento s� ser� efetivado mediante <b>Apresenta��o da Documenta��o</b> junto ao ${ configSistema['siglaUnidadeGestoraGraduacao'] }.</label>			
					</c:if>
					<c:if test="${trancamentoPrograma.obj.discente.graduacao && trancamentoPrograma.obj.posteriori}">
						<h:selectBooleanCheckbox id="estaCienteGradPosteri" value="#{trancamentoPrograma.estaCiente}"/> 
						<label for="form:estaCienteGradPosteri">Estou ciente das condi��es acima e desejo prosseguir com a solicita��o de trancamento de programa para o per�odo ${trancamentoPrograma.obj.ano}.${trancamentoPrograma.obj.periodo}.</label>			
					</c:if>
					<c:if test="${trancamentoPrograma.obj.discente.stricto}">
						<h:selectBooleanCheckbox id="estaCienteStricto" value="#{trancamentoPrograma.estaCiente}"/> 
						<label for="form:estaCienteStricto"><b>Estou Ciente</b> que o trancamento s� ser� efetivado mediante aprova��o da Coordena��o do seu curso.</label>							
					</c:if>
				</div>
				<br>
				<div align="center">
					<h:commandButton value="Continuar >>" action="#{trancamentoPrograma.iniciarSolicitacao }"/>
				</div>
	</h:form>
</f:view><%@include file="/WEB-INF/jsp/include/rodape.jsp"%>