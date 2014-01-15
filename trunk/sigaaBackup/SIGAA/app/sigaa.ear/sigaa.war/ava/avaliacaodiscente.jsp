<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<br/><br/>
<table class="listagem">
<thead>
<tr><th>DIMENS�O 1</th><th>ITENS</th><th>DIM0001</th><th>DIM0002</th><th>DIM0003</th></tr>
</thead>
<tr><td rowspan="9"><strong>ATUA��O<br/> DID�TICA E<br/> POSTURA<br/> PROFISSIONAL<br/> DO PROFESSOR<br/> QUANTO �(AO):</strong></td><td>1.1 - Comparecimento �s aulas</td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>1.2 - Cumprimento do hor�rio das aulas do in�cio ao fim</td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>1.3 - Cumprimento do programa da disciplina</td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>1.4 - Clareza na apresenta��o do conte�do</td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>1.5 - Utiliza��o de metodologias que facilitem o aprendizado</td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>1.6 - Incentivo � participa��o dos alunos nas aulas</td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>1.7 - Disponibilidade para tirar d�vidas dos alunos durante as aulas</td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>1.8 - Disponibilidade para atender aos alunos fora do hor�rio das aulas</td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>1.9 - Coer�ncia entre o n�vel de exig�ncia nas avalia��es e o conte�do dado</td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td><td><%@ include file="combo.jsp" %></td></tr>
</table>

<br/>

<table class="listagem">
<thead>
<tr><th>DIMENS�O 3</th><th>Assinale apenas uma alternativa que considere mais importante</th></tr>
</thead>
<tr><td rowspan="9"><strong>INFRA-ESTRUTURA<br/> DA ${ configSistema['siglaInstituicao'] }</strong></td><td><input type="radio"/> 3.1 - Amplia��o do acervo da biblioteca</td></tr>
<tr><td><input type="radio"/> 3.2 - Amplia��o do espa�o f�sico da biblioteca</td></tr>
<tr><td><input type="radio"/> 3.3 - Constru��o de mais espa�os de conviv�ncia</td></tr>
<tr><td><input type="radio"/> 3.4 - Melhoria das salas de aula</td></tr>
<tr><td><input type="radio"/> 3.5 - Melhoria dos laborat�rios</td></tr>
<tr><td><input type="radio"/> 3.6 - Outra. Citar: <input type="text" size="60"/></td></tr>
</table>

<br/>

<table class="listagem">
<thead>
<tr><th>4 - ASSINALE "SIM" OU "N�O" NAS QUEST�ES SEGUINTES</th><th></th></tr>
</thead>
<tr><td>4.1 - O professor disponibiliza o programa da disciplina na primeira semana de aulas?</td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>4.2 - O professor divulga as notas de uma avalia��o antes da avalia��o seguinte?</td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>4.3 - O professor discute com os alunos os resultados das avalia��es, esclarecendo suas d�vidas?</td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>4.4 - O professor estimula a busca de fontes alternativas de informa��es?</td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>4.5 - Voc� tem orientador acad�mico?</td><td><%@ include file="combo.jsp" %></td></tr>
<tr><td>4.6 - Voc� teve orienta��o acad�mica neste semestre letivo?</td><td><%@ include file="combo.jsp" %></td></tr>
</table>

<br/>

<table class="listagem">
<tr><td rowspan="9"><strong>4.7.<br/> PARTICIPA��O EM<br/> ATIVIDADES</td><td><strong>Assinale a(s) atividade(s) que voc� est� participando neste per�odo letivo</strong></td></tr>
<tr><td><input type="checkbox"/> 4.7.1 - Movimento estudantil</td></tr>
<tr><td><input type="checkbox"/> 4.7.2 - Projeto de Pesquisa</td></tr>
<tr><td><input type="checkbox"/> 4.7.3 - Projeto de Extens�o</td></tr>
<tr><td><input type="checkbox"/> 4.7.4 - Inicia��o � doc�ncia (monitoria)</td></tr>
<tr><td><input type="checkbox"/> 4.7.5 - Apresenta��o de trabalho em Semin�rio/Congresso</td></tr>
<tr><td><input type="checkbox"/> 4.7.6 - Outra. Citar: <input type="text" size="60"/></td></tr>
</table>

<br/>

<table class="listagem">
<tr><td rowspan="9"><strong>5.<br/> TRANCAMENTO DE<br/> DISCIPLINA(S)</td><td>5.1 - Realizou trancamento de disciplina(s) neste per�odo letivo? </td><td>Sim</td></tr>
<tr><td>5.2 - Efetuou trancamento de quantas disciplinas? </td><td> 2</td></tr>
<tr><td colspan="2">5.3 - Relacione a(s) raz�o(�es) para o(s) trancamento(s)</td></tr>
<tr><td colspan="2"><table><tr><td>DIM0001</td><td><textarea cols="70"></textarea></td></tr></table> </td></tr>
<tr><td colspan="2"><table><tr><td>DIM0001</td><td><textarea cols="70"></textarea></td></tr></table> </td></tr>
</table>

<br/>

<table class="listagem">
<thead>
<tr><th style="text-align: center">Espa�o destinado para coment�rios adicionais</th></tr></thead>
<tr><td><textarea cols="100" rows="10"></textarea></td></tr>
</table>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp" %>