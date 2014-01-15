<%@include file="/ava/cabecalho.jsp"%>
<%@taglib prefix="cewolf" uri="/tags/cewolf"%>

<f:view>
	<%@include file="/ava/menu.jsp"%>
	<h:form>

		<fieldset>
			<legend>Estat�sticas de Notas dos Alunos</legend>

			<jsp:useBean id="estatisticasNotas" class="br.ufrn.sigaa.ava.jsf.EstatisticasNotas" scope="page" />
			<jsp:useBean id="estatisticasNotasPostProcessor" class="br.ufrn.sigaa.ava.jsf.EstatisticasNotasPostProcessor" scope="page" />
			
			<div class="descricaoOperacao">
				Os gr�ficos abaixo mostram um histograma de notas das unidades da turma. O histograma mostra 
				as notas de 0 a 10 e quantos alunos tiraram essas notas (arredondando os valores). Abaixo
				dos gr�ficos, � poss�vel ver a m�dia e desvio padr�o de cada unidade. 
			</div>
			
			<center>
			${estatisticasNotasMBean.validarDados}
			<c:if test="${empty estatisticasNotasMBean.unidadesComNota && estatisticasNotasMBean.mostrarMedias }">
				<h1>Unidades</h1>
				<p class="vazio">N�o foram lan�adas notas em nenhuma unidade.</p><br />
			</c:if>
			<c:forEach var="unid" items="${ estatisticasNotasMBean.unidadesComNota }">
				<h1>Unidade ${ unid }</h1>
				<cewolf:chart id="pieChart" type="verticalbar3d" showlegend="false" xaxislabel="Notas" yaxislabel="Qtd. de Alunos">
					<cewolf:colorpaint color="#D3E1F1" />
					<cewolf:data>
						<cewolf:producer id="estatisticasNotas">
							<cewolf:param name="idTurma" value="${ estatisticasNotasMBean.turma.id }" />
							<cewolf:param name="unidade" value="${ unid }" />
						</cewolf:producer>
					</cewolf:data>
					<cewolf:chartpostprocessor id="estatisticasNotasPostProcessor" />
				</cewolf:chart>
				<cewolf:img chartid="pieChart" renderer="/cewolf" width="600" height="360" />
				<c:if test="${ unid == 1 }">
					<br/>M�dia: <ufrn:format type="valor" valor="${ estatisticasNotasMBean.mediaUnidade1 }" />
					<br/>Desvio Padr�o: <ufrn:format type="valor" valor="${ estatisticasNotasMBean.desvioUnidade1 }" />
				</c:if>
				<c:if test="${ unid == 2 }">
					<br/>M�dia: <ufrn:format type="valor" valor="${ estatisticasNotasMBean.mediaUnidade2 }" />
					<br/>Desvio Padr�o: <ufrn:format type="valor" valor="${ estatisticasNotasMBean.desvioUnidade2 }" />
				</c:if>
				<c:if test="${ unid == 3 }">
					<br/>M�dia: <ufrn:format type="valor" valor="${ estatisticasNotasMBean.mediaUnidade3 }" />
					<br/>Desvio Padr�o: <ufrn:format type="valor" valor="${ estatisticasNotasMBean.desvioUnidade3 }" />
				</c:if>
				<br/>&nbsp;<br/>&nbsp;<br/>	
			</c:forEach>
			
			<c:if test="${estatisticasNotasMBean.mostrarGraficoRecuperacao }">
				<h1>Recupera��o</h1>
				<cewolf:chart id="pieChart" type="verticalbar3d" showlegend="false" xaxislabel="Notas" yaxislabel="Qtd. de Alunos">
					<cewolf:colorpaint color="#D3E1F1" />
					<cewolf:data>
						<cewolf:producer id="estatisticasNotas">
							<cewolf:param name="idTurma" value="${ estatisticasNotasMBean.turma.id }" />
							<cewolf:param name="recup" value="${ true }" />
						</cewolf:producer>
					</cewolf:data>
					<cewolf:chartpostprocessor id="estatisticasNotasPostProcessor" />
				</cewolf:chart>
				<cewolf:img chartid="pieChart" renderer="/cewolf" width="600" height="360" />	
				<c:if test="${estatisticasNotasMBean.mostrarMedias }">
					<br/>M�dia: <ufrn:format type="valor" valor="${ estatisticasNotasMBean.mediaRecup }" />
					<br/>Desvio Padr�o: <ufrn:format type="valor" valor="${ estatisticasNotasMBean.desvioRecup }" />
				</c:if>
				<br/>&nbsp;<br/>&nbsp;<br/>
			</c:if>
			
			<h1>M�dia Final</h1>
			<cewolf:chart id="pieChart" type="verticalbar3d" showlegend="false" xaxislabel="Notas" yaxislabel="Qtd. de Alunos">
				<cewolf:colorpaint color="#D3E1F1" />
				<cewolf:data>
					<cewolf:producer id="estatisticasNotas">
						<cewolf:param name="idTurma" value="${ estatisticasNotasMBean.turma.id }" />
						<cewolf:param name="media" value="${ true }" />
					</cewolf:producer>
				</cewolf:data>
				<cewolf:chartpostprocessor id="estatisticasNotasPostProcessor" />
			</cewolf:chart>
			<cewolf:img chartid="pieChart" renderer="/cewolf" width="600" height="360" />	
			<c:if test="${estatisticasNotasMBean.mostrarMedias }">	
				<br/>M�dia: <ufrn:format type="valor" valor="${ estatisticasNotasMBean.mediaFinal }" />
				<br/>Desvio Padr�o: <ufrn:format type="valor" valor="${ estatisticasNotasMBean.desvioFinal }" />
			</c:if>
			<br/>
			
			</center>
			
		</fieldset>

	</h:form>

</f:view>

<%@include file="/ava/rodape.jsp"%>