<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<STYLE TYPE="text/css">
.quebraPagina {
page-break-after: always;
}
</STYLE>

<f:view>
	<h2>Rel�torio do Registro de Evolu��o da Crian�a</h2>

	<c:set var="discente" value="#{registroEvolucaoCriancaMBean.obj.discente}"/>
	<c:set var="turma" value="#{registroEvolucaoCriancaMBean.obj.turma}"/>
	<div id="parametrosRelatorio">
		<table>
			<tr>
				<td width="100%">
					<b>Turma:</b> ${turma.disciplina.nome } &nbsp;<b>Ano:</b> ${turma.ano } &nbsp;<b>Turno:</b> ${turma.descricaoHorario }
				</td>
			</tr>
			<tr>
				<td><b>Matr�cula:</b> ${discente.matricula }</td>
			</tr>
			<tr>
				<td><b>Aluno:</b> ${discente.pessoa.nome } <b>Nascimento:</b> <ufrn:format type="data" valor="${discente.pessoa.dataNascimento}" /></td>
			</tr>
			<tr>
				<td><b>M�e:</b> ${discente.pessoa.nomeMae } </td>
			</tr>
			<tr>
				<td><b>Pai:</b> ${discente.pessoa.nomePai } </td>
			</tr>
			<tr>
				<td><b>Professores:</b> ${turma.docentesNomes }</td>
			</tr>
			<tr>
				<td style="text-align: justify; ">
					<br/><br/><br/>
					<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;O objetivo deste registro � possibilitar aos professores(as) e aos pais o
					acompanhamento da evolu��o da crian�a na escola: suas aprendizagens, dificuldades, e
					conquistas, tendo em vista os objetivos definidos para o grupo do qual ela faz parte e
					para ela mesma. Compreendemos que o desenvolvimento do indiv�duo d�-se de forma
					global, n�o compartimentada. Entretanto, para objetiva��o do registro, as observa��es
					ou constata��es sobre a crian�a ser�o agrupadas em duas grandes �reas dada �
					rela��o existentes entre elas, a saber; �rea de desenvolvimento s�cio-afetivo e �rea de
					conhecimento (Linguagem Oral e Escrita, M�tem�tica, Arte, Educa��o F�sica e Ci�ncias
					Naturais e Sociais).
					</p>
					<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Sendo o desenvolvimento um processo de intera��o, constru��o e reconstru��o
					incessante e n�o linear, as observa��es descritas n�o definem a crian�a, mas a
					caracterizam em uma fase definida da sua vida, ou seja, expressam como ele mostrou-se
					em um determinado per�odo, estando sujeita a constantes transforma��es. Devem,
					portanto, servir como um referencial � a��o da escola e da fam�lia, no sentido de ajudar a
					crian�a a evoluir em seu desenvolvimento.</p>
					<br/><br/><br/><br/><br/>
					<b>OBS.: Esta ficha de registro pretende apresentar a rela��o existente entre os objetivos
					propostos e as compet�ncias apresentadas pela crian�a. Os aspectos que merecem mais
					aten��o ser�o destacados nos itens de observa��es, dando a possibilidade para que os pais
					possam acompanhar melhor a crian�a, percebendo os avan�os e as dificuldades.</b>
				</td>
			</tr>
		</table>
	</div>
	<br/>
	<div class="quebraPagina"></div>
	
	<table class="tabelaRelatorio" width="100%" border="1">
			<c:forEach items="#{registroEvolucaoCriancaMBean.formulario.areas}" var="area">
				<c:if test="${area.subareas != null and empty area.subareas}">
					<c:if test="${area.bloco != null}">
						<tr>
							<td style="text-align: inherit;"><b>${area.bloco.descricaoOrdem}</b></td>
						</tr>
					</c:if>
					<tr>
						<td style="text-align: inherit;"><b>${area.descricaoOrdem}</b></td>
					</tr>
					<tr>
						<td>
							<table class="tabelaRelatorio" width="100%" border="1">
								<thead>
									<tr>
										<th style="text-align: inherit;"> CONTE�DOS/OBJETIVOS </th>
										<th width="6%" style="text-align: center; "> 1� BIM </th>
										<th width="7%" style="text-align: center; "> 2� BIM </th>
										<th width="6%" style="text-align: center; "> 3� BIM </th>
										<th width="7%" style="text-align: center; "> 4� BIM </th>
									</tr>
								</thead>
							</table>
						</td>
					</tr>
					<td>
						<table class="tabelaRelatorio" width="100%">
							<c:forEach items="#{area.conteudos}" var="conteudo">
								<tr>
									<td width="25%" style="text-align: inherit; border: 1px solid #888;" colspan="5"> ${conteudo.descricaoOrdem} </td>
									<td>
										<table width="100%">
											<c:forEach items="#{conteudo.objetivos}" var="objetivo">
												<tr class="relatorioBody">
													<td width="60%" style="text-align: left; border: 1px solid #888; padding: 3;"> ${objetivo.descricaoOrdem} </td>
													<td width="8%" style="text-align: center; border: 1px solid #888; padding: 3;"> ${objetivo.bimestres[0].descricaoResultado} </td>
													<td width="8%" style="text-align: center; border: 1px solid #888; padding: 3;"> ${objetivo.bimestres[1].descricaoResultado} </td>
													<td width="8%" style="text-align: center; border: 1px solid #888; padding: 3;"> ${objetivo.bimestres[2].descricaoResultado} </td>
													<td width="8%" style="text-align: center; border: 1px solid #888; padding: 3;"> ${objetivo.bimestres[3].descricaoResultado} </td>
												</tr>
											</c:forEach>
										</table>
									</td>
								</tr>
							</c:forEach>
						</table>
						<table border="1" width="100%">
							<tr>
								<td align="left" colspan="6"><b>Legenda:</b></td>
							</tr>
							<tr>
								<td align="center">${area.legendaFormaAvaliacao}</td>
							</tr>
						</table>
						<c:if test="${empty area.subareas}">
							</table>
							<br/>
							<table class="tabelaRelatorio" width="100%" border="1">
							<div class="quebraPagina"></div>
						</c:if>
					</td>
				</c:if>
			</c:forEach>
	</table>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>